package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.ArtForm
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.model.UserProfile
import com.example.karunada_kala.model.UserRole
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.KarnatakaRed
import com.example.karunada_kala.ui.theme.Parchment
import com.example.karunada_kala.ui.theme.TextSecondary

@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    onAddArtForm: () -> Unit,
    onEditArtForm: (String) -> Unit,
    onViewArtForm: (String) -> Unit,
    onAddArtisan: () -> Unit,
    onEditArtisan: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: KalaViewModel = viewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val users by viewModel.users.collectAsState()
    val artForms by viewModel.artForms.collectAsState()
    val artisans by viewModel.artisans.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val uiError by viewModel.uiError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiError) {
        uiError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers()
        viewModel.refreshData()
    }

    Scaffold(
        topBar = {
            KalaTopBar(
                title = "Admin Dashboard",
                onProfileClick = onProfileClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Parchment
    ) { padding ->
        @OptIn(ExperimentalMaterial3Api::class)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = selectedTab, containerColor = Color.White, contentColor = KarnatakaRed) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Users", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Art Forms", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("Artisans", modifier = Modifier.padding(16.dp))
                    }
                }

                when (selectedTab) {
                    0 -> UserManagementTab(
                        users = users,
                        onDelete = { uid -> viewModel.deleteUser(uid) },
                        onRoleChange = { uid, role -> viewModel.updateUserRole(uid, role) }
                    )
                    1 -> ArtFormManagementTab(artForms, onAddArtForm, onEditArtForm, onViewArtForm, { id -> viewModel.deleteArtForm(id) })
                    2 -> ArtisanManagementTab(artisans, onAddArtisan, onEditArtisan, { id -> viewModel.deleteArtisan(id) })
                }
            }
        }
    }
}

@Composable
fun UserManagementTab(
    users: List<UserProfile>,
    onDelete: (String) -> Unit,
    onRoleChange: (String, UserRole) -> Unit
) {
    var userToDelete by remember { mutableStateOf<UserProfile?>(null) }

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete ${userToDelete?.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete(userToDelete!!.uid)
                    userToDelete = null 
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(users) { user ->
            var showRoleDialog by remember { mutableStateOf(false) }

            if (showRoleDialog) {
                RoleSelectionDialog(
                    currentRole = user.role,
                    onRoleSelected = { newRole ->
                        onRoleChange(user.uid, newRole)
                        showRoleDialog = false
                    },
                    onDismiss = { showRoleDialog = false }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, fontWeight = FontWeight.Bold)
                        Text(user.email, color = TextSecondary, fontSize = 12.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showRoleDialog = true }
                        ) {
                            Text("Role: ${user.role}", color = KarnatakaRed, fontSize = 12.sp)
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp).padding(start = 4.dp), tint = KarnatakaRed)
                        }
                    }
                    IconButton(onClick = { userToDelete = user }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change User Role") },
        text = {
            Column {
                UserRole.entries.forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoleSelected(role) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = role == currentRole, onClick = { onRoleSelected(role) })
                        Spacer(Modifier.width(8.dp))
                        Text(role.name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ArtFormManagementTab(
    artForms: List<ArtForm>,
    onAddArtForm: () -> Unit,
    onEditArtForm: (String) -> Unit,
    onViewArtForm: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var afToDelete by remember { mutableStateOf<ArtForm?>(null) }

    if (afToDelete != null) {
        AlertDialog(
            onDismissRequest = { afToDelete = null },
            title = { Text("Delete Art Form") },
            text = { Text("Delete ${afToDelete?.name}? This will remove it from the catalog for all users.") },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete(afToDelete!!.id)
                    afToDelete = null 
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { afToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = onAddArtForm,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KarnatakaRed)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Add New Art Form")
        }

        Spacer(Modifier.height(16.dp))
        Text("Current Catalog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = KarnatakaRed)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(artForms) { af ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(af.name, fontWeight = FontWeight.Bold)
                            Text(af.region, color = TextSecondary, fontSize = 12.sp)
                        }
                        if (af.isAiGenerated) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Generated", tint = KarnatakaRed, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                        }
                        IconButton(onClick = { onViewArtForm(af.id) }) {
                            Icon(Icons.Default.Visibility, contentDescription = "View Art Form", tint = KarnatakaRed)
                        }
                        IconButton(onClick = { onEditArtForm(af.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Art Form", tint = KarnatakaRed)
                        }
                        IconButton(onClick = { afToDelete = af }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Art Form", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArtisanManagementTab(
    artisans: List<Artisan>,
    onAddArtisan: () -> Unit,
    onEditArtisan: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var artisanToDelete by remember { mutableStateOf<Artisan?>(null) }

    if (artisanToDelete != null) {
        AlertDialog(
            onDismissRequest = { artisanToDelete = null },
            title = { Text("Delete Artisan") },
            text = { Text("Delete ${artisanToDelete?.name}? They will be removed from the map and catalog.") },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete(artisanToDelete!!.id)
                    artisanToDelete = null 
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { artisanToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = onAddArtisan,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = KarnatakaRed)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Add New Artisan")
        }

        Spacer(Modifier.height(16.dp))
        Text("Current Artisans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = KarnatakaRed)
        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(artisans) { artisan ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(artisan.name, fontWeight = FontWeight.Bold)
                            Text(artisan.district, color = TextSecondary, fontSize = 12.sp)
                            Text(artisan.type.name, color = KarnatakaRed, fontSize = 10.sp)
                        }
                        IconButton(onClick = { onEditArtisan(artisan.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Artisan", tint = KarnatakaRed)
                        }
                        IconButton(onClick = { artisanToDelete = artisan }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Artisan", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}
