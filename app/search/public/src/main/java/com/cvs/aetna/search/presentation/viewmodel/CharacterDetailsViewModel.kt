package com.cvs.aetna.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvs.aetna.search.domain.usecase.CharacterDetailsUseCase
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
}

sealed class CharacterDetailsEvent {
    data class ShowError(val message: String?) : CharacterDetailsEvent()
}

private const val HANDLED_EXCEPTION = "Handled Exception"
private const val USE_CASE_EXCEPTION = "UseCase Exception"

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val characterDetailsUseCase: CharacterDetailsUseCase,
    private val telemetryService: TelemetryService,
) : ViewModel() {

    private val _state = MutableStateFlow<CharacterDetailsUiState>(
        CharacterDetailsUiState.Loading,
    )
    val state: StateFlow<CharacterDetailsUiState> = _state

    private val _actions = Channel<CharacterDetailAction>(capacity = UNLIMITED)
    val actions: SendChannel<CharacterDetailAction> = _actions

    private val eventChannel = Channel<CharacterDetailsEvent>(capacity = UNLIMITED)
    private val _events: SendChannel<CharacterDetailsEvent> = eventChannel

    val events = eventChannel.receiveAsFlow()

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
        updateState { CharacterDetailsUiState.Error(message = msg) }
    }

    private fun handleAction(actions: CharacterDetailAction) {
        when (actions) {
            is CharacterDetailAction.FetchDetails -> {
                fetchCharacterDetails(id = actions.characterId)
            }
        }
    }

    fun sendAction(action: CharacterDetailAction) {
        _actions.trySend(action)
    }

    fun sendEvent(event: CharacterDetailsEvent) {
        _events.trySend(event)
    }

    private fun fetchCharacterDetails(id: String?) {
        viewModelScope.launch(dispatcher + genericErrorHandling) {
            updateState { CharacterDetailsUiState.Loading }
            val result = characterDetailsUseCase.getCharacterDetails(id = id.orEmpty())
            if (result.hasError) {
                onError(type = USE_CASE_EXCEPTION, msg = result.errorMsg)
            } else {
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

    fun updateState(update: (CharacterDetailsUiState) -> CharacterDetailsUiState) {
        _state.value = update(_state.value)
    }
}
