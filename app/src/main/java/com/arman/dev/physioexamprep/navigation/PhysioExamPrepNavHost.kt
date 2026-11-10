package com.arman.dev.physioexamprep.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arman.dev.physioexamprep.core.ui.components.PhysioExamPrepBottomBar

@Composable
fun PhysioExamPrepNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestinationId = resolveDestinationIdByRoute(backStackEntry?.destination?.route?:"Home")
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar(currentDestinationId)){
                PhysioExamPrepBottomBar(
                  currentTabId = currentDestinationId
                )
            }
        }
    ) {paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(
                route = Routes.Home.route
            ){

            }

            composable(
                route = Routes.Home.route
            ){

            }

            composable(
                route = Routes.Home.route
            ){

            }

            composable(
                route = Routes.Home.route
            ){

            }

            composable(
                route = Routes.Home.route
            ){

            }
        }
    }
}

fun resolveDestinationIdByRoute(routes: String? = "Home"): Int{
     return when(routes){
        Routes.Home.route->1
        Routes.Learn.route->2
        Routes.PYQs.route->3
        Routes.Mock.route->4
        Routes.User.route->5
         else -> 6
     }
}

fun shouldShowBottomBar(id:Int = 1) = id in  1..5