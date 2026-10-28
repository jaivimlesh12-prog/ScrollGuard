package com.scrollguard.ui.screen.applimits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scrollguard.data.db.entity.AppLimitEntity
import com.scrollguard.util.Constants

@Composable
fun AppLimitsScreen(
    viewModel: AppLimitsViewModel
) {
    val limits by viewModel.appLimits.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Limit")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "App Usage Limits & Blocker",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure daily screen-time limits. Once reached, apps will be blocked.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (limits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⏱️", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No app limits set yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap the + button to add a daily limit for Instagram, YouTube, or your favorite games.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(limits) { limit ->
                    AppLimitCard(
                        limit = limit,
                        onDelete = { viewModel.deleteLimit(limit.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddLimitDialog(
            onDismiss = { showDialog = false },
            onConfirm = { pkg, name, minutes, strict ->
                viewModel.addOrUpdateLimit(pkg, name, minutes, strict)
                showDialog = false
            }
        )
    }
}

@Composable
fun AppLimitCard(
    limit: AppLimitEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = limit.appName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val minutes = limit.dailyLimitMs / (60 * 1000)
                Text(
                    text = "Daily Limit: $minutes min ${if (limit.isStrict) "• Strict Mode" else "• Flexible Mode"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddLimitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Boolean) -> Unit
) {
    var appName by remember { mutableStateOf("Instagram") }
    var packageName by remember { mutableStateOf(Constants.INSTAGRAM_PACKAGE) }
    var minutesText by remember { mutableStateOf("30") }
    var isStrict by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set App Time Limit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = appName,
                    onValueChange = {
                        appName = it
                        if (it.equals("youtube", ignoreCase = true)) {
                            packageName = Constants.YOUTUBE_PACKAGE
                        } else if (it.equals("instagram", ignoreCase = true)) {
                            packageName = Constants.INSTAGRAM_PACKAGE
                        }
                    },
                    label = { Text("App Name") }
                )

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it },
                    label = { Text("Daily Limit (Minutes)") }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Strict Mode (No 5-min extension)")
                    Switch(checked = isStrict, onCheckedChange = { isStrict = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = minutesText.toIntOrNull() ?: 30
                    onConfirm(packageName, appName, minutes, isStrict)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
