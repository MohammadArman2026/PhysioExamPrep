package com.arman.dev.physioexamprep.navigation

sealed class NavigationRoute(val route : String) {
    object HomeScreen : NavigationRoute("home_screen")
    object PYQScreen : NavigationRoute("PYQScreen")
}