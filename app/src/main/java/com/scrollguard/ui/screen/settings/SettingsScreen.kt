package com.scrollguard.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scrollguard.util.PermissionHelper

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Preferences",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // ── 1. Daily Limits ──────────────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎯 Daily Targets & Limits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Reel & Short Limit: ${state.dailyReelLimit} videos")
                    Slider(
                        value = state.dailyReelLimit.toFloat(),
                        onValueChange = { viewModel.setDailyReelLimit(it.toInt()) },
                        valueRange = 10f..200f,
                        steps = 18
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Daily Phone Unlock Limit: ${state.dailyPickupLimit} pickups")
                    Slider(
                        value = state.dailyPickupLimit.toFloat(),
                        onValueChange = { viewModel.setDailyPickupLimit(it.toInt()) },
                        valueRange = 20f..200f,
                        steps = 17
                    )
                }
            }
        }

        // ── 2. Health & Wellbeing ─────────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👁️ Digital Health",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("20-20-20 Eye Strain Reminder", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Alert every 20 minutes to rest your eyes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.isEyeStrainEnabled,
                            onCheckedChange = { viewModel.setEyeStrainEnabled(it) }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bedtime Wind Down", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Auto-activates focus mode at 11:00 PM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = state.isBedtimeModeEnabled,
                            onCheckedChange = { viewModel.setBedtimeEnabled(it) }
                        )
                    }
                }
            }
        }

        // ── 3. Time Value Calculator ──────────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💰 Time-to-Money Calculator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Set your hourly rate to visualize the economic cost of scrolling and gaming.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = if (state.hourlyWage > 0) state.hourlyWage.toString() else "",
                        onValueChange = { input ->
                            val wage = input.toFloatOrNull() ?: 0f
                            viewModel.setHourlyWage(wage)
                        },
                        label = { Text("Hourly Rate ($)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // ── 4. System Permissions Status ──────────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 System Permissions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { PermissionHelper.requestUsageStatsPermission(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Usage Access Settings")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { PermissionHelper.requestAccessibilityPermission(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Accessibility Service Settings")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { PermissionHelper.requestOverlayPermission(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Draw Over Other Apps Settings")
                    }
                }
            }
        }
    }
}
