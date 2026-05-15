package com.example.karunada_kala.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanProfileScreen(
    artisan: Artisan,
    onBackClick: () -> Unit,
    onWorkshopSignUp: () -> Unit,
    viewModel: KalaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val events by viewModel.events.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    if (selectedImageUrl != null) {
        FullScreenImageDialog(imageUrl = selectedImageUrl!!) { selectedImageUrl = null }
    }

    // Filter events for this specific artisan
    val artisanEvents = events.filter { it.organiserId == artisan.id || it.organiser == artisan.name }

    Scaffold(
        topBar = { KalaTopBar(artisan.name, showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // Header
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)
                    .background(Brush.verticalGradient(listOf(KarnatakaRed, KarnatakaRed.copy(alpha = 0.6f))))) {
                    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.size(80.dp).clip(CircleShape), contentAlignment = Alignment.Center) {
                            if (artisan.imageUrl.isNotBlank()) {
                                SubcomposeAsyncImage(
                                    model = artisan.imageUrl,
                                    contentDescription = artisan.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                    loading = { CircularProgressIndicator(color = KarnatakaYellow, modifier = Modifier.size(24.dp)) },
                                    error = {
                                        Box(modifier = Modifier.fillMaxSize().background(KarnatakaYellow), contentAlignment = Alignment.Center) {
                                            Text(artisan.name.first().toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize().background(KarnatakaYellow), contentAlignment = Alignment.Center) {
                                    Text(artisan.name.first().toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(artisan.name, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(artisan.artForms.joinToString(" · "), color = KarnatakaYellow)
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = KarnatakaYellow.copy(alpha = 0.25f)) {
                        Text("📍 ${artisan.district}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = KarnatakaRed)
                    Spacer(Modifier.height(6.dp))
                    Text(artisan.bio, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(20.dp))

                    // Tap to Call
                    Button(onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${artisan.phone}"))) },
                        modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KarnatakaRed)) {
                        Icon(Icons.Filled.Call, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Tap to Call", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(Modifier.height(12.dp))

                    // Message on WhatsApp
                    Button(onClick = {
                        val url = "https://api.whatsapp.com/send?phone=${artisan.phone}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                        modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { // WhatsApp Green
                        Icon(Icons.Filled.Chat, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Message on WhatsApp", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(Modifier.height(24.dp))

                    // PRD §5.3: Product Gallery (up to 6 images)
                    Text("Product Gallery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = KarnatakaRed)
                    Spacer(Modifier.height(10.dp))
                    
                    if (artisan.productImages.isEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) {
                                Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(12.dp))
                                    .background(Brush.verticalGradient(listOf(KarnatakaYellow.copy(0.4f), KarnatakaRed.copy(0.2f)))),
                                    contentAlignment = Alignment.Center) { Text("🎨", fontSize = 32.sp) }
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(end = 20.dp)
                        ) {
                            items(artisan.productImages.take(6)) { img ->
                                Card(
                                    modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedImageUrl = img },
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = img,
                                        contentDescription = "Product",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) } },
                                        error = { Box(Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) { Icon(Icons.Default.Close, null) } }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    // Upcoming Workshops
                    Text("Upcoming Workshops", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = KarnatakaRed)
                    Spacer(Modifier.height(10.dp))
                    
                    if (artisanEvents.isEmpty()) {
                        Text("No upcoming workshops scheduled.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    } else {
                        Column {
                            artisanEvents.forEach { ev ->
                                EventCard(ev.name, ev.artForm, ev.district, ev.dateTime, ev.organiser, imageUrl = ev.imageUrl)
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))

                    OutlinedButton(onClick = onWorkshopSignUp, modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = KarnatakaRed)) {
                        Text("Register for a Workshop", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f))) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clickable { onDismiss() },
                contentScale = ContentScale.Fit,
                loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) } }
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }
}
