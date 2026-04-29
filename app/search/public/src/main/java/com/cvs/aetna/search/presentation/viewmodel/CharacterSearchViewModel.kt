package com.cvs.aetna.search.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvs.aetna.search.domain.model.CharacterDetails
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import com.cvs.aetna.search.logger.TelemetryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CharacterSearchUiState {
    object Empty : CharacterSearchUiState()
    object Loading : CharacterSearchUiState()
    data class Success(val charactersList: List<CharacterDetails>) : CharacterSearchUiState()
    data class Error(val message: String?) : CharacterSearchUiState()
}

sealed class CharacterSearchAction {
    data class Search(val name: String) : CharacterSearchAction()
    data class OnCharacterClick(val characterId: String) : CharacterSearchAction()

    data object ReachedEndOfList : CharacterSearchAction()
}

sealed class CharacterSearchEvent {
    data class NavigateToDetails(val characterId: String) : CharacterSearchEvent()
}

private const val HANDLED_EXCEPTION = "Handled Exception"
private const val USE_CASE_EXCEPTION = "UseCase Exception"

@HiltViewModel
class CharacterSearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val characterListUseCase: CharacterListUseCase,
    private val dispatcher: CoroutineDispatcher,
    private val telemetryService: TelemetryService,
) : ViewModel() {

    private val _state = MutableStateFlow<CharacterSearchUiState>(CharacterSearchUiState.Empty)
    val state: StateFlow<CharacterSearchUiState> = _state

    private val _actions = Channel<CharacterSearchAction>(capacity = UNLIMITED)
    val actions: SendChannel<CharacterSearchAction> = _actions
    private val eventChannel = Channel<CharacterSearchEvent>(capacity = UNLIMITED)
    private val _events: SendChannel<CharacterSearchEvent> = eventChannel
    val events: Flow<CharacterSearchEvent> = eventChannel.receiveAsFlow()

    private val genericErrorHandling = CoroutineExceptionHandler { _, throwable ->
        onError(type = HANDLED_EXCEPTION, msg = throwable.message)
    }

    private fun onError(
        type: String,
        msg: String?,
    ) {
        telemetryService.logEvent(
            eventName = "CharacterSearchError$type",
            properties = mapOf(
                "errorType" to type,
                "errorMessage" to (msg ?: "Unknown error"),
            ),
        )
        updateState { CharacterSearchUiState.Error(message = msg) }
    }

    init {
        _actions.consumeAsFlow().onEach { actions ->
            handleAction(actions)
        }.launchIn(viewModelScope)
    }

    fun sendAction(action: CharacterSearchAction) {
        _actions.trySend(action)
    }

    fun sendEvent(event: CharacterSearchEvent) {
        _events.trySend(event)
    }

    private fun handleAction(action: CharacterSearchAction) {
        when (action) {
            is CharacterSearchAction.Search -> {
                fetchCharacterListByName(action.name)
            }

            is CharacterSearchAction.OnCharacterClick -> {
                sendEvent(CharacterSearchEvent.NavigateToDetails(characterId = action.characterId))
            }

            else -> {
            }
        }
    }

    private fun fetchCharacterListByName(
        characterName: String,
    ) {
        viewModelScope.launch(dispatcher + genericErrorHandling) {
            updateState { CharacterSearchUiState.Loading }
            characterListUseCase.getCharacterList(name = characterName)
                .let { result ->
                    if (result.hasError) {
                        onError(type = USE_CASE_EXCEPTION, msg = result.errorMsg)
                    } else {
                        updateState {
                            CharacterSearchUiState.Success(
                                charactersList = result.characters ?: emptyList(),
                            )
                        }
                    }
                }
        }
    }

    fun updateState(update: (CharacterSearchUiState) -> CharacterSearchUiState) {
        _state.value = update(_state.value)
    }
}
