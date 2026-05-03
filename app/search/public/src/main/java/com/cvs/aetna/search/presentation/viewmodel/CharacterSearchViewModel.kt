package com.cvs.aetna.search.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvs.aetna.search.analytics.CharacterListAdobeTagUseCase
import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.usecase.CharacterListUseCase
import com.cvs.aetna.search.logger.TelemetryService
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.model.CharacterListUiModel
import com.cvs.aetna.search.presentation.ui.model.UiText
import com.cvs.aetna.search.presentation.ui.model.getFilter
import com.cvs.aetna.search.presentation.ui.model.saveFilter
import com.cvs.aetna.search.presentation.ui.model.toDomain
import com.cvs.aetna.search.presentation.ui.model.toUiModelList
import com.cvs.aetna.search.presentation.ui.model.toUiText
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchEvent.NavigateToDetails
import com.cvs.aetna.search.pub.R
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
    data class Success(
        val charactersList: List<CharacterListUiModel>,
        val totalCount: Int = 0,
        val hasNextPage: Boolean = false,
        val isLoadingMore: Boolean = false,
    ) : CharacterSearchUiState()

    data class Error(val message: UiText) : CharacterSearchUiState()
}

sealed class CharacterSearchAction {
    data class Search(val name: String) : CharacterSearchAction()
    data class OnCharacterClick(val characterId: String) : CharacterSearchAction()

    data class OnFilterUpdate(val filterUiState: CharacterFilterUiState) : CharacterSearchAction()
    data object OnResetFilter : CharacterSearchAction()
    data object ReachedEndOfList : CharacterSearchAction()

    data object OnPageLoad : CharacterSearchAction()
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
    private val adobeTagUseCase: CharacterListAdobeTagUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CharacterSearchUiState>(CharacterSearchUiState.Empty)
    val state: StateFlow<CharacterSearchUiState> = _state

    private val _actions = Channel<CharacterSearchAction>(capacity = UNLIMITED)
    val actions: SendChannel<CharacterSearchAction> = _actions
    private val eventChannel = Channel<CharacterSearchEvent>(capacity = UNLIMITED)
    val events: Flow<CharacterSearchEvent> = eventChannel.receiveAsFlow()

    private val _filterState = MutableStateFlow(savedStateHandle.getFilter())
    val filterState: StateFlow<CharacterFilterUiState> = _filterState

    private var nextLoadUrl: String? = null
    private var isLoadingMoreInternal = false

    private var isPageLoadEventSent = false

    private val genericErrorHandling = CoroutineExceptionHandler { _, throwable ->
        onError(type = HANDLED_EXCEPTION, error = CharacterError.Unknown(throwable.message))
    }

    private fun onError(
        type: String,
        error: CharacterError?,
    ) {
        telemetryService.logEvent(
            eventName = "CharacterSearchError$type",
            properties = mapOf(
                "errorType" to type,
                "errorMessage" to (error.toString()),
            ),
        )
        adobeTagUseCase.tagOnPageErrorEvent(message = (error.toString()))
        resetInternalUIState()
        updateState {
            CharacterSearchUiState.Error(
                message = error?.toUiText() ?: UiText.StringResource(resId = R.string.error_unknown),
            )
        }
    }

    private fun resetInternalUIState() {
        isLoadingMoreInternal = false
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
        eventChannel.trySend(event)
    }

    private fun handleAction(action: CharacterSearchAction) {
        when (action) {
            is CharacterSearchAction.Search -> {
                adobeTagUseCase.tagOnSearchAction(characterName = action.name)
                _filterState.value = _filterState.value.copy(name = action.name)
                fetchCharacterList()
            }

            is CharacterSearchAction.OnCharacterClick -> {
                adobeTagUseCase.tagOnClickImage(characterId = action.characterId)
                sendEvent(NavigateToDetails(characterId = action.characterId))
            }

            is CharacterSearchAction.OnFilterUpdate -> {
                adobeTagUseCase.tagOnFilterApplyAction()
                _filterState.value = _filterState.value.copy(
                    status = action.filterUiState.status,
                    type = action.filterUiState.type,
                    species = action.filterUiState.species,
                )
                fetchCharacterList()
            }

            CharacterSearchAction.OnResetFilter -> {
                adobeTagUseCase.tagOnFilterResetAction()
                _filterState.value = _filterState.value.copy(
                    status = null,
                    type = null,
                    species = null,
                    name = "",
                )
                fetchCharacterList()
            }

            CharacterSearchAction.ReachedEndOfList -> {
                viewModelScope.launch(dispatcher + genericErrorHandling) {
                    if (!tryLockLoadMore()) return@launch
                    adobeTagUseCase.tagOnPageLoadMoreAction()
                    nextLoadUrl?.let { loadMoreUrl ->
                        fetchMoreCharacterList(url = loadMoreUrl)
                    }
                }
            }

            CharacterSearchAction.OnPageLoad -> {
                if (!isPageLoadEventSent) {
                    adobeTagUseCase.tagOnSearchScreenLoadEvent()
                    isPageLoadEventSent = true
                }
            }
        }
    }
    private fun tryLockLoadMore(): Boolean {
        val state = _state.value as? CharacterSearchUiState.Success ?: return false

        if (isLoadingMoreInternal ||
            nextLoadUrl.isNullOrBlank() ||
            state.charactersList.size >= state.totalCount
        ) {
            return false
        }

        isLoadingMoreInternal = true
        return true
    }
    private fun fetchCharacterList() {
        nextLoadUrl = null
        viewModelScope.launch(dispatcher + genericErrorHandling) {
            updateState { CharacterSearchUiState.Loading }
            storeFilterToSaveHandle()
            handleResult(
                characterListUseCase.getCharacterList(
                    characterSearch = _filterState.value.toDomain(),
                ),
                previousList = emptyList(),
            )
        }
    }

    private suspend fun fetchMoreCharacterList(url: String) {
        val currentList =
            (state.value as? CharacterSearchUiState.Success)?.charactersList.orEmpty()
        updateSuccessState { current ->
            current.copy(isLoadingMore = true)
        }

        try {
            val result = characterListUseCase.getMoreCharacterList(url)
            handleResult(result, currentList)
        } finally {
            isLoadingMoreInternal = false
            updateSuccessState { current ->
                current.copy(isLoadingMore = false)
            }
        }
    }
    private inline fun updateSuccessState(
        update: (CharacterSearchUiState.Success) -> CharacterSearchUiState.Success,
    ) {
        val current = _state.value
        if (current is CharacterSearchUiState.Success) {
            _state.value = update(current)
        }
    }

    private fun handleResult(
        result: CharacterList,
        previousList: List<CharacterListUiModel> = emptyList(),
    ) {
        if (result.hasError) {
            onError(type = USE_CASE_EXCEPTION, error = result.errorMsg)
            return
        }
        val newItems = (result.characters ?: emptyList()).toUiModelList()
        val updatedList = previousList + newItems
        nextLoadUrl = result.nextPageUrl
        if (_state.value !is CharacterSearchUiState.Success) {
            updateState {
                CharacterSearchUiState.Success(
                    charactersList = updatedList,
                    totalCount = result.totalCount,
                    hasNextPage = result.nextPageUrl != null,
                    isLoadingMore = false,
                )
            }
        } else {
            updateSuccessState {
                it.copy(
                    charactersList = updatedList,
                    totalCount = result.totalCount,
                    hasNextPage = result.nextPageUrl != null,
                    isLoadingMore = false,
                )
            }
        }
    }

    private fun storeFilterToSaveHandle() {
        savedStateHandle.saveFilter(_filterState.value)
    }

    fun updateState(update: (CharacterSearchUiState) -> CharacterSearchUiState) {
        _state.value = update(_state.value)
    }
}
