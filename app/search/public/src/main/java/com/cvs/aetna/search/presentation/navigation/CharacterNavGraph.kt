package com.cvs.aetna.search.presentation.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun CharacterNavGraph(
    modifier: Modifier = Modifier,
    characterNavigator: CharacterGraphProvider,
) {
    val navHostController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navHostController,
            startDestination = CHARACTER_LANDING_ROOT,
            modifier = modifier,
        ) {
            with(characterNavigator) {
                characterNavGraph(
                    navController = navHostController,
                    startDestination = Route.Search.route,
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterNavGraphPreview() {
}
