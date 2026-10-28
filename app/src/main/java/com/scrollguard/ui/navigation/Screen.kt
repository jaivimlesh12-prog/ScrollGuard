package com.scrollguard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Destination routes for Jetpack Compose Navigation.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.BarChart)
    object AppLimits : Screen("app_limits", "Limits", Icons.Default.Timer)
    object Focus : Screen("focus", "Focus", Icons.Default.SelfImprovement)
    object Challenges : Screen("challenges", "Challenges", Icons.Default.EmojiEvents)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Default.Home)
}
