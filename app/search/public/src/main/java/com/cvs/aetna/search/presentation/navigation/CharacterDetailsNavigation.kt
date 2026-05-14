package com.cvs.aetna.search.presentation.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cvs.aetna.search.presentation.ui.screen.CharacterDetailsRouteScreen
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailAction
import com.cvs.aetna.search.presentation.viewmodel.CharacterDetailsViewModel

fun NavGraphBuilder.characterDetailsNavigation(
    sharedTransitionScope: SharedTransitionScope,
    navController: NavController,
) {
    composable(
        Route.Details.route,
        arguments = listOf(
            navArgument(Route.Details.ARG_ID) {
                defaultValue = ""
            },
        ),
    ) { navBackEntry ->
        val backStackEntry = remember(navBackEntry) {
            navController.getBackStackEntry(Route.Details.route)
        }
        val characterDetailsViewModel: CharacterDetailsViewModel =
            hiltViewModel<CharacterDetailsViewModel>(backStackEntry)
        val id = navBackEntry.arguments?.getString(Route.Details.ARG_ID)

        CharacterDetailsRouteScreen(
            id = id,
            animatedVisibilityScope = this,
            sharedTransitionScope = sharedTransitionScope,
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

fun NavController.navigateToCharacterDetails(
    characterId: String,
    popUpTo: String,
) {
    navigate(Route.Details.createRoute(characterId)) {
        popUpTo(popUpTo)
        launchSingleTop = true
        restoreState = true
    }
}
