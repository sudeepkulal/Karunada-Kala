package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.ui.components.KalaPrimaryButton
import com.example.karunada_kala.ui.theme.*

@Composable
fun ConfirmationScreen(onBackToMap: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Parchment), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(SuccessGreen), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Registration Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text("Your workshop interest has been registered. The artisan will reach out to you soon on the phone number provided.",
                style = MaterialTheme.typography.bodyLarge, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(36.dp))
            KalaPrimaryButton("Back to Map", onClick = onBackToMap)
        }
    }
}