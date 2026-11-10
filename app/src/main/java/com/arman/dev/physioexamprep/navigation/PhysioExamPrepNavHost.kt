package com.arman.dev.physioexamprep.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arman.dev.physioexamprep.core.ui.components.PhysioExamPrepBottomBar
import com.arman.dev.physioexamprep.feature.home.ui.HomeScreen
import com.arman.dev.physioexamprep.feature.learn.ui.LearnScreen
import com.arman.dev.physioexamprep.feature.mocks.ui.MockScreen
import com.arman.dev.physioexamprep.feature.pyq.ui.PYQScreen
import com.arman.dev.physioexamprep.feature.user.ui.UserScreen

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
                    modifier = Modifier.navigationBarsPadding().padding(bottom = 4.dp),
                  currentTabId = currentDestinationId,
                    onTabClick = { tabId ->
                        val route = resolveRouteByDestinationId(tabId) ?: return@PhysioExamPrepBottomBar
                        if (route == backStackEntry?.destination?.route) return@PhysioExamPrepBottomBar
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                HomeScreen()
            }

            composable(
                route = Routes.Learn.route
            ){
                LearnScreen(
                   onSubjectClick = {

                   }
                )
            }

            composable(
                route = Routes.PYQs.route
            ){
                PYQScreen()
            }

            composable(
                route = Routes.Mock.route
            ){
                MockScreen()
            }

            composable(
                route = Routes.User.route
            ){
                UserScreen()
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

fun resolveRouteByDestinationId(id: Int): String? {
    return when (id) {
        1 -> Routes.Home.route
        2 -> Routes.Learn.route
        3 -> Routes.PYQs.route
        4 -> Routes.Mock.route
        5 -> Routes.User.route
        else -> null
    }
}

fun shouldShowBottomBar(id:Int = 1) = id in  1..5
