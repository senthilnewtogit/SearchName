package com.cvs.aetna.search.presentation.navigation

import androidx.compose.foundation.layout.Column
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
import com.cvs.aetna.search.presentation.ui.screen.CharacterDetailsRouteScreen
import com.cvs.aetna.search.presentation.ui.screen.CharacterListScreen
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchAction
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchEvent
import com.cvs.aetna.search.presentation.viewmodel.CharacterSearchViewModel

@Composable
fun CharacterNavGraph(
    modifier: Modifier = Modifier,
    characterSearchViewModel: CharacterSearchViewModel = hiltViewModel(),
) {
    val navHostController = rememberNavController()

    LaunchedEffect(Unit) {
        characterSearchViewModel.events.collect { event ->
            when (event) {
                is CharacterSearchEvent.NavigateToDetails -> {
                    navHostController.navigate(Route.Details.createRoute(event.characterId))
                }
            }
        }
    }

    val state = characterSearchViewModel.state.collectAsStateWithLifecycle()
    Column(modifier = modifier) {
        NavHost(
            navController = navHostController,
            startDestination = Route.Search.route,

        ) {
            composable(Route.Search.route) {
                CharacterListScreen(
                    characterSearchUiState = state.value,
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
                CharacterDetailsRouteScreen(id = id)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterNavGraphPreview() {
    // Navigation preview is best handled by previewing individual screens
    // due to ViewModel dependencies, but we've added CharacterListScreenPreview.
}
