package com.arman.dev.physioexamprep.navigation

sealed class Routes(val route: String) {
    data object Home: Routes("Home")
    data object Learn : Routes("Learn")

    data object Mock : Routes("Mocks")

    data object PYQs : Routes("PYQs")
    data object User : Routes("User")
}