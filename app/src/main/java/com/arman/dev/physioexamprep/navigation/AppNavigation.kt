package com.arman.dev.physioexamprep.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arman.dev.physioexamprep.R
import com.arman.dev.physioexamprep.core.ui.theme.appBackGround
import com.arman.dev.physioexamprep.core.ui.theme.cardColor
import com.arman.dev.physioexamprep.feature.module.presentation.ModuleScreen
import com.arman.dev.physioexamprep.feature.module.presentation.component.ModuleTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
    ){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavigationRoute.ModuleScreen.route ,
        NavigationRoute.PYQScreen.route
    )

    Scaffold (
        modifier = modifier.fillMaxSize(),
        containerColor = appBackGround,
        topBar = {
            when(currentRoute){
                NavigationRoute.ModuleScreen.route -> {
                    ModuleTopBar(
                        title = stringResource(R.string.app_title) ,
                        navigationIcon = R.drawable.physio_exam_icon
                    )
                }
                NavigationRoute.PYQScreen.route -> {

                }
            }

        },
        bottomBar = {
            if (showBottomBar){
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = {route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ){paddingValues ->
        NavHost(
            startDestination = NavigationRoute.ModuleScreen.route ,
            navController = navController ,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            composable(route = NavigationRoute.ModuleScreen.route){
                ModuleScreen {

                }
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
            selected = currentRoute == NavigationRoute.ModuleScreen.route,
            onClick = { onNavigate(NavigationRoute.ModuleScreen.route) },
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