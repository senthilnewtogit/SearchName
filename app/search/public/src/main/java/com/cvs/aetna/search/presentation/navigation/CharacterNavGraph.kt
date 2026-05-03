package com.cvs.aetna.search.presentation.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cvs.aetna.search.presentation.ui.model.CharacterFilterUiState
import com.cvs.aetna.search.presentation.ui.screen.CharacterDetailsRouteScreen
import com.cvs.aetna.search.presentation.ui.screen.CharacterListScreen
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailAction
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsViewModel
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchAction
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchEvent
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchViewModel

@Composable
fun CharacterNavGraph(
    modifier: Modifier = Modifier,
    characterSearchViewModel: CharacterSearchViewModel = hiltViewModel(),
) {
    val navHostController = rememberNavController()

    LaunchedEffect(characterSearchViewModel) {
        characterSearchViewModel.events.collect { event ->
            when (event) {
                is CharacterSearchEvent.NavigateToDetails -> {
                    navHostController.navigate(Route.Details.createRoute(event.characterId)) {
                        popUpTo(Route.Search.route)
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
    val uiState = characterSearchViewModel.state.collectAsStateWithLifecycle()
    val searchFilterState = characterSearchViewModel.filterState.collectAsStateWithLifecycle()
    SharedTransitionLayout {
        NavHost(
            navController = navHostController,
            startDestination = Route.Search.route,
            modifier = modifier,
        ) {
            composable(Route.Search.route) {
                CharacterListScreen(
                    characterSearchUiState = uiState.value,
                    onCharacterClick = { characterId ->
                        characterSearchViewModel.sendAction(
                            CharacterSearchAction.OnCharacterClick(
                                characterId,
                            ),
                        )
                    },
                    onCharacterType = { characterTyped ->
                        characterSearchViewModel.sendAction(
                            CharacterSearchAction.Search(characterTyped),
                        )
                    },
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    searchFilterState = searchFilterState.value,
                    onResetFilter = {
                        characterSearchViewModel.sendAction(
                            CharacterSearchAction.OnResetFilter,
                        )
                    },
                    onFilterUpdate = { searchFilterState: CharacterFilterUiState ->
                        characterSearchViewModel.sendAction(
                            CharacterSearchAction.OnFilterUpdate(filterUiState = searchFilterState),
                        )
                    },
                    onEndOfList = {
                        characterSearchViewModel.sendAction(
                            CharacterSearchAction.ReachedEndOfList,
                        )
                    },
                    onPageLoad = {
                        characterSearchViewModel.sendAction(
                            CharacterSearchAction.OnPageLoad,
                        )
                    },
                )
            }
            composable(
                Route.Details.route,
                arguments = listOf(
                    navArgument(Route.Details.ARG_ID) {
                        defaultValue = ""
                    },
                ),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString(Route.Details.ARG_ID)
                val characterDetailsViewModel: CharacterDetailsViewModel =
                    hiltViewModel<CharacterDetailsViewModel>(backStackEntry)
                CharacterDetailsRouteScreen(
                    id = id,
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    characterDetailsViewModel = characterDetailsViewModel,
                    shareOnClick = {
                        characterDetailsViewModel.sendAction(CharacterDetailAction.Share)
                    },
                    onPageLoad = {
                        characterDetailsViewModel.sendAction(CharacterDetailAction.OnPageLoad)
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterNavGraphPreview() {
}
