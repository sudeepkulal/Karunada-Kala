package com.example.karunada_kala.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.UserRole
import com.example.karunada_kala.model.WorkshopRegistration
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRegistrationsScreen(
    onBackClick: () -> Unit,
    viewModel: KalaViewModel
) {
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(userProfile) {
        if (userProfile?.role == UserRole.ARTISAN) {
            viewModel.fetchRegistrations(userProfile?.uid)
        } else if (userProfile?.role == UserRole.ADMIN) {
            viewModel.fetchRegistrations(null)
        }
    }

    Scaffold(
        topBar = { KalaTopBar("Workshop Registrations", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { 
                if (userProfile?.role == UserRole.ARTISAN) viewModel.fetchRegistrations(userProfile?.uid)
                else viewModel.fetchRegistrations(null)
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (registrations.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No registrations found.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(registrations.sortedByDescending { it.submittedAt }) { reg ->
                        RegistrationCard(reg)
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
fun RegistrationCard(reg: WorkshopRegistration) {
    val context = LocalContext.current
    val dateStr = try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(reg.submittedAt))
    } catch (e: Exception) { "" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = KarnatakaRed, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(reg.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(50), color = KarnatakaYellow.copy(alpha = 0.2f)) {
                    Text(reg.artForm, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), 
                        fontSize = 11.sp, color = KarnatakaRed, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            RegistrationInfoItem(Icons.Default.Phone, reg.phone)
            RegistrationInfoItem(Icons.Default.Email, reg.email)
            RegistrationInfoItem(Icons.Default.Event, "Preferred: ${reg.preferredDate}")
            
            if (reg.message.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().background(Parchment, RoundedCornerShape(8.dp)).padding(10.dp)) {
                    Text("💬 ${reg.message}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))
            
            Text("Registered on: $dateStr", fontSize = 11.sp, color = TextSecondary)
            if (reg.artisanName.isNotBlank()) {
                Text("For: ${reg.artisanName}", fontSize = 11.sp, color = KarnatakaRed.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${reg.phone}"))) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Call", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${reg.email}"))) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Email", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun RegistrationInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}
