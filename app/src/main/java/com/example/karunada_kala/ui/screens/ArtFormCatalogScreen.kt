package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.navigation.Screen
import com.example.karunada_kala.ui.theme.*

import androidx.compose.material.icons.filled.Add
import com.example.karunada_kala.data.KalaViewModel

import com.example.karunada_kala.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtFormCatalogScreen(
    navController: NavController,
    onArtFormClick: (String) -> Unit,
    onAddArtForm: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: KalaViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    val artForms by viewModel.artForms.collectAsState()
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val filtered = artForms.filter { query.isBlank() || it.name.contains(query, true) || it.region.contains(query, true) }
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
        topBar = { KalaTopBar("Explore Art Forms", onProfileClick = onProfileClick) },
        bottomBar = { KalaBottomBar(Screen.ArtFormCatalog.route) { r -> navController.navigate(r) { launchSingleTop = true; popUpTo(Screen.Home.route) } } },
        floatingActionButton = {
            if (userProfile?.role == UserRole.ADMIN) {
                FloatingActionButton(onClick = onAddArtForm, containerColor = KarnatakaRed, contentColor = Color.White) {
                    Icon(Icons.Filled.Add, "Add Art Form")
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
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = query, onValueChange = { query = it },
                    placeholder = { Text("Search art forms or region…") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = kalaFieldColors())
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filtered, key = { it.id }) { af ->
                        ArtFormCard(af.name, af.region, af.summary, imageUrl = af.imageUrl) { onArtFormClick(af.id) }
                    }
                }
            }
        }
    }
}
