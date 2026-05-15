package com.example.karunada_kala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.karunada_kala.R
import com.example.karunada_kala.ui.navigation.bottomNavItems
import com.example.karunada_kala.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalaTopBar(title: String, showBack: Boolean = false, onBackClick: () -> Unit = {}, onProfileClick: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showBack) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).padding(end = 8.dp)
                    )
                }
                Text(title, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            if (showBack) IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            if (onProfileClick != null) {
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = KarnatakaRed)
    )
}

@Composable
fun KalaBottomBar(currentRoute: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick  = { onTabSelected(item.route) },
                icon     = { Icon(item.icon, contentDescription = item.label) },
                label    = { Text(item.label, fontSize = 11.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = KarnatakaRed,
                    selectedTextColor   = KarnatakaRed,
                    indicatorColor      = KarnatakaYellow.copy(alpha = 0.2f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

@Composable
fun ArtFormCard(name: String, region: String, summary: String, modifier: Modifier = Modifier, imageUrl: String = "", onClick: () -> Unit) {
    Card(
        modifier  = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = KarnatakaYellow, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .background(Brush.horizontalGradient(listOf(KarnatakaRed.copy(alpha = 0.7f), KarnatakaYellow.copy(alpha = 0.5f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name.first().toString(), fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(KarnatakaRed.copy(alpha = 0.7f), KarnatakaYellow.copy(alpha = 0.5f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.first().toString(), fontSize = 40.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(region, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(summary, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        }
    }
}

@Composable
fun EventCard(
    name: String,
    artForm: String,
    district: String,
    dateTime: String,
    organiser: String,
    modifier: Modifier = Modifier,
    imageUrl: String = "",
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = { CircularProgressIndicator(color = KarnatakaYellow, modifier = Modifier.size(16.dp), strokeWidth = 2.dp) },
                        error = {
                            Box(modifier = Modifier.fillMaxSize().background(KarnatakaYellow), contentAlignment = Alignment.Center) {
                                Text(artForm.first().toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                            }
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(KarnatakaYellow), contentAlignment = Alignment.Center) {
                        Text(artForm.first().toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                ArtFormBadge(artForm)
                Spacer(Modifier.height(4.dp))
                Text("📍 $district", style = MaterialTheme.typography.bodyMedium)
                Text("🕐 $dateTime",  style = MaterialTheme.typography.bodyMedium)
                Text("👤 $organiser", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ArtFormBadge(artForm: String) {
    Surface(shape = RoundedCornerShape(50), color = KarnatakaYellow.copy(alpha = 0.3f)) {
        Text(artForm, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
fun AIGeneratedBadge() {
    Surface(shape = RoundedCornerShape(50), color = KarnatakaYellow.copy(alpha = 0.25f)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextPrimary)
            Spacer(Modifier.width(4.dp))
            Text("Generated by AI", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
        }
    }
}

@Composable
fun KalaPrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        enabled  = enabled,
        shape    = RoundedCornerShape(10.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = KarnatakaRed)
    ) { Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(title, style = MaterialTheme.typography.titleLarge, modifier = modifier.padding(vertical = 8.dp),
        fontWeight = FontWeight.Bold, color = KarnatakaRed)
}

@Composable
fun KalaFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) KarnatakaRed else CardSurface,
        modifier = Modifier.clickable(onClick = onClick),
        shadowElevation = if (selected) 2.dp else 0.dp,
        border = if (!selected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            color = if (selected) Color.White else TextPrimary,
            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title   = { Text("Logout", fontWeight = FontWeight.Bold) },
        text    = { Text("Are you sure you want to logout?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Logout", color = KarnatakaRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
