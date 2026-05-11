package com.cvs.aetna.search.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.model.CharacterListUiModel
import com.cvs.aetna.search.presentation.ui.screen.components.CharacterListGridUiContent
import com.cvs.aetna.search.presentation.ui.screen.components.ErrorUIElement
import com.cvs.aetna.search.presentation.ui.screen.components.LoadingIndicator
import com.cvs.aetna.search.presentation.ui.screen.components.NoResultFound
import com.cvs.aetna.search.presentation.ui.screen.components.OnPageLoad
import com.cvs.aetna.search.presentation.ui.screen.components.SearchTextField
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchUiState

@Composable
fun CharacterListScreen(
    characterSearchUiState: CharacterSearchUiState,
    onCharacterClick: (String) -> Unit,
    onCharacterType: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    searchFilterState: CharacterFilterUiState,
    onResetFilter: () -> Unit,
    onFilterUpdate: (CharacterFilterUiState) -> Unit,
    onEndOfList: () -> Unit,
    onPageLoad: () -> Unit,
) {
    ShowListScreen(
        onCharacterClick = onCharacterClick,
        onSearchQueryChanged = onCharacterType,
        characterSearchUiState = characterSearchUiState,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        searchFilterState = searchFilterState,
        onFilterReset = onResetFilter,
        onFilterUpdate = onFilterUpdate,
        onEndOfList = onEndOfList,
        onPageLoad = onPageLoad,
    )
}

private const val CHARACTER_SEARCH_SCREEN = "character_search_screen"

@Composable
private fun ShowListScreen(
    onSearchQueryChanged: (String) -> Unit,
    onCharacterClick: (String) -> Unit,
    characterSearchUiState: CharacterSearchUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    searchFilterState: CharacterFilterUiState,
    onFilterReset: () -> Unit,
    onFilterUpdate: (CharacterFilterUiState) -> Unit,
    onEndOfList: () -> Unit,
    onPageLoad: () -> Unit,
) {
    var searchQuery by remember(searchFilterState) { mutableStateOf(searchFilterState.name) }
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    val closeFilterSheet = {
        showFilterSheet = false
    }
    OnPageLoad(onPageLoad = onPageLoad)
    Column(
        modifier = Modifier.fillMaxSize().testTag(CHARACTER_SEARCH_SCREEN),
        horizontalAlignment = Alignment.Start,
    ) {
        SearchTextField(
            value = searchQuery,
            onValueChange = {
                onSearchQueryChanged(it)
                searchQuery = it
            },
            modifier = Modifier.fillMaxWidth(),
            onFilterClick = {
                showFilterSheet = true
            },
        )
        when (characterSearchUiState) {
            CharacterSearchUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is CharacterSearchUiState.Error -> {
                ErrorUIElement(uiText = characterSearchUiState.message, onRetry = {
                    onSearchQueryChanged(searchQuery)
                })
            }

            is CharacterSearchUiState.Success -> {
                ShowCharacterList(
                    characterList = characterSearchUiState.charactersList,
                    onCharacterClick = onCharacterClick,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    onEndOfList = onEndOfList,
                    shouldLoadMore = characterSearchUiState.hasNextPage,
                    isLoadingMore = characterSearchUiState.isLoadingMore,
                    totalCount = characterSearchUiState.totalCount,
                )
            }

            CharacterSearchUiState.Empty -> {
                NoResultFound()
            }
        }
    }
    if (showFilterSheet) {
        FilterBottomSheetScreen(
            onDismiss = closeFilterSheet,
            onApply = {
                onFilterUpdate(it)
                closeFilterSheet()
            },
            characterFilterUiState = searchFilterState,
            onReset = onFilterReset,
        )
    }
}

@Composable
fun ShowCharacterList(
    characterList: List<CharacterListUiModel>,
    onCharacterClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onEndOfList: () -> Unit,
    shouldLoadMore: Boolean,
    isLoadingMore: Boolean,
    totalCount: Int,
) {
    if (characterList.isEmpty()) {
        NoResultFound()
    } else {
        CharacterListGridUiContent(
            list = characterList,
            onCharacterClick = onCharacterClick,
            animatedVisibilityScope = animatedVisibilityScope,
            sharedTransitionScope = sharedTransitionScope,
            onEndOfList = onEndOfList,
            shouldLoadMore = shouldLoadMore,
            isLoadingMore = isLoadingMore,
            totalCount = totalCount,

        )
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterListScreenPreview() {
    val sampleCharacters = listOf(
        CharacterListUiModel(
            id = 1,
            name = "Rick Sanchez",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        ),
        CharacterListUiModel(
            id = 2,
            name = "Morty Smith",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
        ),
    )
    SharedTransitionLayout {
        CharacterListScreen(
            characterSearchUiState = CharacterSearchUiState.Success(sampleCharacters),
            onCharacterClick = {},
            onCharacterType = {},
            sharedTransitionScope = this@SharedTransitionLayout,
            searchFilterState = CharacterFilterUiState(),
            onResetFilter = {
            },
            onFilterUpdate = {
            },
            onEndOfList = {
            },
            onPageLoad = {
            },
        )
    }
}
