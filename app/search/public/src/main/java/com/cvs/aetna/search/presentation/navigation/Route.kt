package com.cvs.aetna.search.presentation.navigation

sealed class Route(val route: String) {
    object Search : Route("search")
    object Details : Route("details/{Id}") {
        const val ARG_ID = "Id"
        fun createRoute(id: String) = "details/$id"
    }
}
