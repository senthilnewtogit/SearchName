package com.cvs.aetna.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvs.aetna.search.analytics.CharacterDetailsAdobeTagUseCase
import com.cvs.aetna.search.di.IoDispatcher
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.model.ShareData
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase
import com.cvs.aetna.search.domain.usecase.ShareCharacterUseCase
import com.cvs.aetna.search.logger.TelemetryService
import com.cvs.aetna.search.presentation.ui.model.CharacterUIDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CharacterDetailsUiState {
    object Loading : CharacterDetailsUiState()
    data class Success(val characterDetails: CharacterUIDetails) : CharacterDetailsUiState()

    data class Error(val message: String?) : CharacterDetailsUiState()
}

sealed class CharacterDetailAction {
    data class FetchDetails(val characterId: String?) : CharacterDetailAction()
    object Share : CharacterDetailAction()
    object ShareAppNotFound : CharacterDetailAction()
    object OnPageLoad : CharacterDetailAction()
}

sealed class CharacterDetailsEvent {
    data class ShowError(val message: String?) : CharacterDetailsEvent()
    data class ShareImageData(val shareData: ShareData) : CharacterDetailsEvent()
}

private const val HANDLED_EXCEPTION = "Handled Exception"
private const val USE_CASE_EXCEPTION = "UseCase Exception"

const val NO_APP_FOUND = "1000"

const val NO_SHARE_DATA_FOUND = "1001"

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val characterDetailsUseCase: CharacterDetailsUseCase,
    private val telemetryService: TelemetryService,
    private val shareCharacterUseCase: ShareCharacterUseCase,
    private val characterDetailsAdobeTagUseCase: CharacterDetailsAdobeTagUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CharacterDetailsUiState>(
        CharacterDetailsUiState.Loading,
    )
    val state: StateFlow<CharacterDetailsUiState> = _state

    private val _actions = Channel<CharacterDetailAction>(capacity = UNLIMITED)
    val actions: SendChannel<CharacterDetailAction> = _actions

    private val eventChannel = Channel<CharacterDetailsEvent>(capacity = UNLIMITED)
    val events = eventChannel.receiveAsFlow()

    private var cacheCharacterDetails: CharacterDetails? = null

    private var isPageLoadSent = false

    init {
        _actions.consumeAsFlow().onEach { actions ->
            handleAction(actions)
        }.launchIn(viewModelScope)
    }

    private val genericErrorHandling = CoroutineExceptionHandler { _, throwable ->
        onError(type = HANDLED_EXCEPTION, msg = throwable.message)
    }

    private fun onError(
        type: String,
        msg: String?,
    ) {
        telemetryService.logEvent(
            eventName = "CharacterDetailsError:$type",
            properties = mapOf(
                "errorType" to type,
                "errorMessage" to (msg ?: "Unknown error"),
            ),
        )
        characterDetailsAdobeTagUseCase.tagOnError((msg ?: "Unknown error"))
        updateState { CharacterDetailsUiState.Error(message = msg) }
    }

    private fun handleAction(actions: CharacterDetailAction) {
        when (actions) {
            is CharacterDetailAction.FetchDetails -> {
                characterDetailsAdobeTagUseCase.tagOnDetailsFetchAction()
                fetchCharacterDetails(id = actions.characterId)
            }

            is CharacterDetailAction.Share -> {
                characterDetailsAdobeTagUseCase.tagOnDetailsShareAction()
                shareImageMetaData()
            }

            CharacterDetailAction.ShareAppNotFound -> {
                characterDetailsAdobeTagUseCase.tagOnShareAppNotFound()
                sendEvent(CharacterDetailsEvent.ShowError(message = NO_APP_FOUND))
            }

            CharacterDetailAction.OnPageLoad -> {
                if (!isPageLoadSent) {
                    characterDetailsAdobeTagUseCase.tagOnDetailsScreenLoad()
                    isPageLoadSent = true
                }
            }
        }
    }

    private fun shareImageMetaData() {
        viewModelScope.launch(dispatcher + genericErrorHandling) {
            cacheCharacterDetails?.let {
                val shareCharacterImage = shareCharacterUseCase.shareCharacterImage(character = it)
                sendEvent(CharacterDetailsEvent.ShareImageData(shareData = shareCharacterImage))
            } ?: run {
                sendEvent(CharacterDetailsEvent.ShowError(message = NO_SHARE_DATA_FOUND))
            }
        }
    }

    fun sendAction(action: CharacterDetailAction) {
        _actions.trySend(action)
    }

    private fun sendEvent(event: CharacterDetailsEvent) {
        eventChannel.trySend(event)
    }

    private fun fetchCharacterDetails(id: String?) {
        viewModelScope.launch(dispatcher + genericErrorHandling) {
            updateState { CharacterDetailsUiState.Loading }
            val result = characterDetailsUseCase.getCharacterDetails(id = id.orEmpty())
            if (result.hasError) {
                onError(type = USE_CASE_EXCEPTION, msg = result.errorMsg)
            } else {
                cacheCharacterDetails = result
                updateState {
                    CharacterDetailsUiState.Success(
                        characterDetails = CharacterUIDetails(
                            id = result.id,
                            name = result.name?.trim(),
                            status = result.status?.trim(),
                            origin = result.origin?.trim(),
                            species = result.species?.trim(),
                            type = result.type?.trim(),
                            createdAt = result.createdAt?.trim(),
                            imageUrl = result.imageUrl?.trim(),
                            hasError = false,
                            errorMsg = result.errorMsg?.trim(),
                        ),
                    )
                }
            }
        }
    }

    private fun updateState(update: (CharacterDetailsUiState) -> CharacterDetailsUiState) {
        _state.value = update(_state.value)
    }
}
