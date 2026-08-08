package com.disciplineos.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.disciplineos.app.domain.MotivationQuotes
import com.disciplineos.app.ui.dashboard.DashboardScreen
import com.disciplineos.app.ui.home.HomeScreen
import com.disciplineos.app.ui.plans.PlanDetailScreen
import com.disciplineos.app.ui.plans.PlansScreen
import com.disciplineos.app.ui.theme.Black
import com.disciplineos.app.ui.theme.BorderGray
import com.disciplineos.app.ui.theme.NearBlack
import com.disciplineos.app.ui.theme.TextMuted
import com.disciplineos.app.ui.theme.TextPrimary

private object Routes {
    const val TODAY = "today"
    const val PLANS = "plans"
    const val PLAN_DETAIL = "plan/{planId}"
    const val DASHBOARD = "dashboard"

    fun planDetail(id: Long) = "plan/$id"
}

private data class Tab(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun DisciplineOsApp() {
    val navController = rememberNavController()
    val sessionQuote = remember { MotivationQuotes.forSession() }
    val tabs = listOf(
        Tab(Routes.TODAY, "Today", Icons.Outlined.Bolt),
        Tab(Routes.PLANS, "Plans", Icons.Outlined.FitnessCenter),
        Tab(Routes.DASHBOARD, "Stats", Icons.Outlined.Analytics),
    )
    val backStack by navController.currentBackStackEntryAsState()
    val currentDestination = backStack?.destination
    val showBottomBar = currentDestination?.route in setOf(
        Routes.TODAY,
        Routes.PLANS,
        Routes.DASHBOARD,
    )

    Scaffold(
        containerColor = Black,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = NearBlack, contentColor = TextPrimary) {
                    tabs.forEach { tab ->
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = BorderGray,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.TODAY,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.TODAY) {
                HomeScreen(
                    sessionQuote = sessionQuote,
                    onOpenPlans = {
                        navController.navigate(Routes.PLANS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(Routes.PLANS) {
                PlansScreen(
                    onOpenPlan = { id -> navController.navigate(Routes.planDetail(id)) },
                )
            }
            composable(
                route = Routes.PLAN_DETAIL,
                arguments = listOf(navArgument("planId") { type = NavType.LongType }),
            ) {
                PlanDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.DASHBOARD) { DashboardScreen() }
        }
    }
}
