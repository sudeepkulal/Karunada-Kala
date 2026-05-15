package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.navigation.Screen
import com.example.karunada_kala.ui.theme.*

import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.ArtForm
import com.example.karunada_kala.model.KalaEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onArtFormClick: (String) -> Unit,
    onEventClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: KalaViewModel
) {
    val artForms by viewModel.artForms.collectAsState()
    val events by viewModel.events.collectAsState()
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout(onLogout)
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Scaffold(
        topBar = { KalaTopBar("Karunada Kala", onProfileClick = onProfileClick) },
        bottomBar = { KalaBottomBar(Screen.Home.route) { route -> navController.navigate(route) { launchSingleTop = true; popUpTo(Screen.Home.route) } } },
        containerColor = Parchment
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // Hero
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)
                    .background(Brush.horizontalGradient(listOf(KarnatakaRed, KarnatakaRed.copy(alpha = 0.75f))))
                    .padding(24.dp)) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = if (userProfile != null) "ನಮಸ್ಕಾರ, ${userProfile?.name?.split(" ")?.firstOrNull() ?: ""} 👋" else "ನಮಸ್ಕಾರ 👋",
                            fontSize = 16.sp,
                            color = KarnatakaYellow,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("Explore Karnataka's\nLiving Heritage", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 30.sp)
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onMapClick,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Icon(Icons.Filled.Map, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Find Artisans Near You", color = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(20.dp))
                    SectionHeader("Featured Art Forms")
                    Spacer(Modifier.height(8.dp))
                }
                
                if (artForms.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                        Text("Discovering heritage...", color = TextSecondary)
                    }
                } else {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        artForms.take(5).forEach { af ->
                            ArtFormCard(af.name, af.region, af.summary, modifier = Modifier.width(200.dp), imageUrl = af.imageUrl) { onArtFormClick(af.id) }
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionHeader("Upcoming Events")
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onEventClick) { Text("See all", color = KarnatakaRed, fontWeight = FontWeight.Medium) }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    if (events.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardSurface),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No events scheduled this week.", color = TextSecondary, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        events.take(3).forEach { ev ->
                            EventCard(ev.name, ev.artForm, ev.district, ev.dateTime, ev.organiser, imageUrl = ev.imageUrl)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
