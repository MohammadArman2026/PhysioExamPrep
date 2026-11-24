package com.arman.dev.physioexamprep.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arman.dev.physioexamprep.core.domain.model.QuizRequest
import com.arman.dev.physioexamprep.core.ui.components.PhysioExamPrepBottomBar
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.feature.home.presentation.HomeRoute
import com.arman.dev.physioexamprep.feature.learn.presentation.LearnRoute
import com.arman.dev.physioexamprep.feature.mockresult.presentation.MockResultRoute
import com.arman.dev.physioexamprep.feature.mocks.presentation.MocksRoute
import com.arman.dev.physioexamprep.feature.profile.presentation.ProfileRoute
import com.arman.dev.physioexamprep.feature.pyq.presentation.PyqRoute
import com.arman.dev.physioexamprep.feature.quiz.presentation.QuizRoute
import com.arman.dev.physioexamprep.feature.topicpicker.presentation.TopicPickerRoute
import androidx.compose.foundation.background

/**
 * App navigation. The bottom bar floats over the tab screens (as in Figma), so tab screens get
 * [tabContentPadding] to keep their last item clear of it. Screens draw behind the status bar
 * and handle insets themselves.
 */
@Composable
fun PhysioExamPrepNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestinationId = resolveDestinationIdByRoute(backStackEntry?.destination?.route ?: Routes.Home.route)
    val context = LocalContext.current
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // 68dp bar + 12dp gap under it + system nav bar.
    val tabContentPadding = PaddingValues(bottom = 68.dp + 12.dp + navBarInset)

    fun openTab(route: String) = navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
    val openQuiz: (QuizRequest) -> Unit = { navController.navigate(Routes.Quiz.create(it)) }
    val openTopics: (Long) -> Unit = { navController.navigate(Routes.Topics.create(it)) }
    val openResult: (String) -> Unit = { navController.navigate(Routes.MockResult.create(it)) }

    Box(modifier.fillMaxSize().background(AppCanvas)) {
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(Routes.Home.route) {
                HomeRoute(
                    onOpenQuiz = openQuiz,
                    onOpenTopicPicker = openTopics,
                    onOpenLearn = { openTab(Routes.Learn.route) },
                    onOpenProfile = { openTab(Routes.User.route) },
                    contentPadding = tabContentPadding,
                )
            }
            composable(Routes.Learn.route) {
                LearnRoute(onOpenTopicPicker = openTopics, contentPadding = tabContentPadding)
            }
            composable(Routes.PYQs.route) {
                PyqRoute(onOpenQuiz = openQuiz, contentPadding = tabContentPadding)
            }
            composable(Routes.Mock.route) {
                MocksRoute(onOpenQuiz = openQuiz, onOpenResult = openResult, contentPadding = tabContentPadding)
            }
            composable(Routes.User.route) {
                ProfileRoute(
                    onLoggedOut = {
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        openTab(Routes.Home.route)
                    },
                    contentPadding = tabContentPadding,
                )
            }
            composable(Routes.Topics.route, arguments = Routes.Topics.arguments) {
                TopicPickerRoute(onBack = { navController.popBackStack() }, onOpenQuiz = openQuiz)
            }
            composable(Routes.Quiz.route, arguments = Routes.Quiz.arguments) {
                QuizRoute(
                    onBack = { navController.popBackStack() },
                    onOpenResult = { resultId ->
                        navController.navigate(Routes.MockResult.create(resultId)) {
                            popUpTo(Routes.Quiz.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.MockResult.route, arguments = Routes.MockResult.arguments) {
                MockResultRoute(onBack = { navController.popBackStack() }, onOpenTopicPicker = openTopics)
            }
        }

        if (shouldShowBottomBar(currentDestinationId)) {
            PhysioExamPrepBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                currentTabId = currentDestinationId,
                onTabClick = { tabId ->
                    val route = resolveRouteByDestinationId(tabId) ?: return@PhysioExamPrepBottomBar
                    if (route != backStackEntry?.destination?.route) openTab(route)
                },
            )
        }
    }
}

fun resolveDestinationIdByRoute(route: String? = Routes.Home.route): Int = when (route) {
    Routes.Home.route -> 1
    Routes.Learn.route -> 2
    Routes.PYQs.route -> 3
    Routes.Mock.route -> 4
    Routes.User.route -> 5
    else -> 6
}

fun resolveRouteByDestinationId(id: Int): String? = when (id) {
    1 -> Routes.Home.route
    2 -> Routes.Learn.route
    3 -> Routes.PYQs.route
    4 -> Routes.Mock.route
    5 -> Routes.User.route
    else -> null
}

fun shouldShowBottomBar(id: Int = 1) = id in 1..5
