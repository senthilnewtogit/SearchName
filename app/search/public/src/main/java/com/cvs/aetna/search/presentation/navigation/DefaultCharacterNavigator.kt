package com.cvs.aetna.search.presentation.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import javax.inject.Inject

class DefaultCharacterNavigator@Inject constructor() : CharacterGraphProvider {
    override fun NavGraphBuilder.characterNavGraph(
        navController: NavHostController,
        startDestination: String,
        sharedTransitionScope: SharedTransitionScope,
    ) {
        navigation(startDestination = startDestination, route = CHARACTER_LANDING_ROOT) {
            characterListNavigation(
                sharedTransitionScope = sharedTransitionScope,
                navController = navController,
            )
            characterDetailsNavigation(
                sharedTransitionScope = sharedTransitionScope,
                navController = navController,
            )
        }
    }
}
