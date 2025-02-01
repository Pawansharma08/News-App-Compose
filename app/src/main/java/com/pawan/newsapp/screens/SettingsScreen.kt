package com.pawan.newsapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    // SharedPreferences to store the notification state
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var isNotificationsEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true))
    }

    // Save notification setting to SharedPreferences when it changes
    LaunchedEffect(isNotificationsEnabled) {
        sharedPreferences.edit().putBoolean("notifications_enabled", isNotificationsEnabled).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Notification Toggle
            Text(text = "Notifications", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enable Notifications", modifier = Modifier.weight(1f))
                Switch(
                    checked = isNotificationsEnabled,
                    onCheckedChange = { isChecked ->
                        isNotificationsEnabled = isChecked
                        Toast.makeText(context, if (isChecked) "Notifications Enabled" else "Notifications Disabled", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // You can add other settings here if needed
        }
    }
}
