package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.navigation.Screen
import com.example.karunada_kala.ui.theme.*

import com.example.karunada_kala.data.KalaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFeedScreen(
    navController: NavController,
    onPostEvent: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: KalaViewModel
) {
    var selectedArtForm by rememberSaveable { mutableStateOf("All") }
    val artForms by viewModel.artForms.collectAsState()
    val events by viewModel.events.collectAsState()
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val filters = listOf("All") + artForms.map { it.name }
    val filteredEvents = events.filter { selectedArtForm == "All" || it.artForm == selectedArtForm }
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
        topBar = { KalaTopBar("Event Feed", onProfileClick = onProfileClick) },
        bottomBar = { KalaBottomBar(Screen.EventFeed.route) { r -> navController.navigate(r) { launchSingleTop = true; popUpTo(Screen.Home.route) } } },
        floatingActionButton = {
            if (userProfile?.role != com.example.karunada_kala.model.UserRole.EXPLORER) {
                FloatingActionButton(onClick = onPostEvent, containerColor = KarnatakaRed) {
                    Icon(Icons.Filled.Add, "Post Event", tint = Color.White)
                }
            }
        },
        containerColor = Parchment
    ) { padding ->
        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.take(6).forEach { f -> KalaFilterChip(f, selectedArtForm == f) { selectedArtForm = f } }
                }
                if (filteredEvents.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No events for this art form yet.", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredEvents, key = { it.id }) { ev ->
                            EventCard(
                                name = ev.name,
                                artForm = ev.artForm,
                                district = ev.district,
                                dateTime = ev.dateTime,
                                organiser = ev.organiser,
                                imageUrl = ev.imageUrl,
                                onDelete = if (userProfile?.role == com.example.karunada_kala.model.UserRole.ADMIN) {
                                    { viewModel.deleteEvent(ev.id) }
                                } else null
                            )
                        }
                    }
                }
            }
        }
    }
}
