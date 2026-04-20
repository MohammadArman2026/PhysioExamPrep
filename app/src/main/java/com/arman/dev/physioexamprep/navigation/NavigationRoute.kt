package com.arman.dev.physioexamprep.navigation

sealed class NavigationRoute(val route : String) {
    object ModuleScreen : NavigationRoute("module_screen")
    object PYQScreen : NavigationRoute("PYQScreen")
}