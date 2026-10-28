package com.scrollguard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.scrollguard.ui.screen.analytics.AnalyticsScreen
import com.scrollguard.ui.screen.analytics.AnalyticsViewModel
import com.scrollguard.ui.screen.applimits.AppLimitsScreen
import com.scrollguard.ui.screen.applimits.AppLimitsViewModel
import com.scrollguard.ui.screen.challenges.ChallengesScreen
import com.scrollguard.ui.screen.challenges.ChallengesViewModel
import com.scrollguard.ui.screen.dashboard.DashboardScreen
import com.scrollguard.ui.screen.dashboard.DashboardViewModel
import com.scrollguard.ui.screen.focus.FocusModeScreen
import com.scrollguard.ui.screen.focus.FocusViewModel
import com.scrollguard.ui.screen.onboarding.OnboardingScreen
import com.scrollguard.ui.screen.settings.SettingsScreen
import com.scrollguard.ui.screen.settings.SettingsViewModel

@Composable
fun ScrollGuardNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(viewModel = viewModel)
        }

        composable(Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = hiltViewModel()
            AnalyticsScreen(viewModel = viewModel)
        }

        composable(Screen.AppLimits.route) {
            val viewModel: AppLimitsViewModel = hiltViewModel()
            AppLimitsScreen(viewModel = viewModel)
        }

        composable(Screen.Focus.route) {
            val viewModel: FocusViewModel = hiltViewModel()
            FocusModeScreen(viewModel = viewModel)
        }

        composable(Screen.Challenges.route) {
            val viewModel: ChallengesViewModel = hiltViewModel()
            ChallengesScreen(viewModel = viewModel)
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(viewModel = viewModel)
        }
    }
}
