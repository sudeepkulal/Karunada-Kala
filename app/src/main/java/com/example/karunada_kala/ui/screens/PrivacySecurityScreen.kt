package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.Parchment

@Composable
fun PrivacySecurityScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = { KalaTopBar("Privacy & Security", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Your Data Matters", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            PrivacyItem("Data Encryption", "All your personal details and registration data are encrypted and stored securely in Google Cloud (Firebase).")
            PrivacyItem("Location Privacy", "We only use your location to show nearby artisans on the map. We never track your location in the background.")
            PrivacyItem("Account Security", "We use Firebase Authentication to ensure your account remains safe. We do not store your passwords on our own servers.")
            PrivacyItem("Marketing", "We do not sell your personal data to third-party advertisers. Your information is only used within Karunada Kala.")
            
            Spacer(Modifier.height(32.dp))
            Text("For any privacy concerns, contact: privacy@karunadakala.in", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PrivacyItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(description, style = MaterialTheme.typography.bodyMedium)
    }
}
