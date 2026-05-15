package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onRegistrationsClick: () -> Unit = {},
    onMyActivityClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onLogout: () -> Unit,
    viewModel: KalaViewModel
) {
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val profileLoaded by viewModel.profileLoaded.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
            text = { Text("Are you absolutely sure? This will permanently delete your profile and all your data from our servers. You will be logged out immediately.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAccountDialog = false
                    viewModel.deleteOwnAccount(
                        onSuccess = onLogout,
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                }) {
                    Text("Delete Permanently", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // While the very first load hasn't finished yet, show a full-screen loader.
    // Once profileLoaded == true we show the real UI regardless of whether
    // userProfile is null (e.g. user not logged in) or populated.
    if (!profileLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = KarnatakaRed)
        }
        return
    }

    Scaffold(
        topBar = {
            KalaTopBar(
                title = "My Profile",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Parchment
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Header gradient ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(KarnatakaRed, KarnatakaRed.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Avatar circle — shows initial letter or a person icon
                        Surface(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initial = userProfile?.name
                                    ?.trim()
                                    ?.firstOrNull()
                                    ?.toString()
                                    ?.uppercase()
                                if (!initial.isNullOrEmpty()) {
                                    Text(
                                        text = initial,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = KarnatakaRed
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = KarnatakaRed,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Username — show actual name or a clear "Guest" fallback
                        Text(
                            text = userProfile?.name?.takeIf { it.isNotBlank() } ?: "Guest",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userProfile?.email?.takeIf { it.isNotBlank() } ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KarnatakaYellow
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    // Role badge
                    if (userProfile != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = KarnatakaRed.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = userProfile?.role?.toString() ?: "EXPLORER",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = KarnatakaRed,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    // ── Account section ───────────────────────────────────────
                    ProfileSectionHeader("Account")
                    ProfileOptionItem(
                        icon = Icons.Default.Person,
                        title = "Personal Information",
                        onClick = onEditProfileClick
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.History,
                        title = "My Activity",
                        onClick = onMyActivityClick
                    )

                    if (userProfile?.role != com.example.karunada_kala.model.UserRole.EXPLORER) {
                        ProfileOptionItem(
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            title = "Workshop Registrations",
                            onClick = onRegistrationsClick
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── App section ───────────────────────────────────────────
                    ProfileSectionHeader("Application")
                    ProfileOptionItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        isSwitch = true,
                        switchValue = isDarkMode,
                        onSwitchChange = { viewModel.toggleDarkMode(it) }
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Notifications,
                        title = "Notification Settings",
                        onClick = onNotificationsClick
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Security,
                        title = "Privacy & Security",
                        onClick = onPrivacyClick
                    )
                    ProfileOptionItem(
                        icon = Icons.Default.Info,
                        title = "About Karunada Kala",
                        onClick = onAboutClick
                    )

                    Spacer(Modifier.height(40.dp))

                    // ── Logout button ─────────────────────────────────────────
                    Button(
                        onClick = { viewModel.logout(onLogout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KarnatakaRed,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Version 1.0.0",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(20.dp))

                    if (userProfile != null) {
                        TextButton(
                            onClick = { showDeleteAccountDialog = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Delete Account", color = Color.Red.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = KarnatakaRed,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    isSwitch: Boolean = false,
    switchValue: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        onClick = if (!isSwitch) onClick else ({})
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Parchment),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = KarnatakaRed, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (isSwitch) {
                Switch(
                    checked = switchValue,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = KarnatakaRed,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = TextSecondary.copy(alpha = 0.3f)
                    )
                )
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}