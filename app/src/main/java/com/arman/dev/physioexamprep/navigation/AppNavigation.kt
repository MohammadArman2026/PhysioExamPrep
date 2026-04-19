package com.arman.dev.physioexamprep.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.theme.appBackGround
import com.arman.dev.physioexamprep.core.ui.theme.cardColor

@Composable
fun AppNavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
    ){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavigationRoute.HomeScreen.route ,
        NavigationRoute.PYQScreen.route
    )

    Scaffold (
        modifier = modifier,
        containerColor = appBackGround,
        bottomBar = {
            if (showBottomBar){
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = {route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ){paddingValues ->
        NavHost(
            startDestination = NavigationRoute.HomeScreen.route ,
            navController = navController ,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = NavigationRoute.HomeScreen.route){

            }
            composable(route = NavigationRoute.PYQScreen.route){

            }
        }
    }
}



@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar (
        containerColor = cardColor
    ){

        NavigationBarItem(
            selected = currentRoute == NavigationRoute.HomeScreen.route,
            onClick = { onNavigate(NavigationRoute.HomeScreen.route) },
            icon = { Icon(painterResource(R.drawable.module),
                null ,
                tint = MaterialTheme.colorScheme.primary) },
            label = { Text("MODULE" ,
                color = MaterialTheme.colorScheme.primary) }
        )

        NavigationBarItem(
            selected = currentRoute == NavigationRoute.PYQScreen.route,
            onClick = { onNavigate(NavigationRoute.PYQScreen.route) },
            icon = { Icon(painterResource(R.drawable.pyq) ,
                contentDescription = null ,
                tint = MaterialTheme.colorScheme.primary)},
            label = { Text("PYQ" ,
                color = MaterialTheme.colorScheme.primary) }
        )
    }
}