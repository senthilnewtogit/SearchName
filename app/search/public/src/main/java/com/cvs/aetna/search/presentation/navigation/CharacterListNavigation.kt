package com.cvs.aetna.search.presentation.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cvs.aetna.search.presentation.ui.screen.CharacterListScreenRoute

fun NavGraphBuilder.characterListNavigation(
    sharedTransitionScope: SharedTransitionScope,
    navController: NavController,
) {
    composable(Route.Search.route) {
        CharacterListScreenRoute(
            animatedVisibilityScope = this,
            sharedTransitionScope = sharedTransitionScope,
            onNavigateToDetails = { characterId: String ->
                navController.navigateToCharacterDetails(
                    characterId = characterId,
                    popUpTo = Route.Search.route,
                )
            },
        )
    }
}
