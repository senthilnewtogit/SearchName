package com.cvs.aetna.search.presentation.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface CharacterGraphProvider {

    fun NavGraphBuilder.characterNavGraph(
        navController: NavHostController,
        startDestination: String,
        sharedTransitionScope: SharedTransitionScope,
    )
}
