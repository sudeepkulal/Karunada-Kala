package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.Parchment

@Composable
fun NotificationSettingsScreen(onBackClick: () -> Unit) {
    var eventsEnabled by remember { mutableStateOf(true) }
    var workshopsEnabled by remember { mutableStateOf(true) }
    var marketingEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { KalaTopBar("Notification Settings", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text("Control how you receive updates from Karunada Kala.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))

            NotificationToggle("Upcoming Events", "Get notified when new events are posted", eventsEnabled) { eventsEnabled = it }
            NotificationToggle("Workshop Updates", "Stay informed about your registrations", workshopsEnabled) { workshopsEnabled = it }
            NotificationToggle("Cultural News", "Weekly highlights from Karnataka's art scene", marketingEnabled) { marketingEnabled = it }
        }
    }
}

@Composable
fun NotificationToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
