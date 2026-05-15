package com.example.karunada_kala.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.karunada_kala.model.ArtForm
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.theme.*

@Composable
fun ArtFormDetailScreen(
    artForm: ArtForm,
    onBackClick: () -> Unit,
    viewModel: com.example.karunada_kala.data.KalaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val generatingAiFor by viewModel.generatingAiFor.collectAsState()
    val isGenerating = generatingAiFor.contains(artForm.id)

    Scaffold(
        topBar = {
            KalaTopBar(
                title = artForm.name,
                showBack = true,
                onBackClick = onBackClick,
                onProfileClick = null // No profile icon needed here
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing Karnataka art form: ${artForm.name} on Karunada Kala!")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                containerColor = KarnatakaRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        },
        containerColor = Parchment
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                if (artForm.imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = artForm.imageUrl,
                        contentDescription = artForm.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = KarnatakaYellow)
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize()
                                    .background(Brush.verticalGradient(listOf(KarnatakaRed.copy(alpha = 0.8f), KarnatakaYellow.copy(alpha = 0.5f)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(artForm.name.first().toString(), fontSize = 80.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(KarnatakaRed.copy(alpha = 0.8f), KarnatakaYellow.copy(alpha = 0.5f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(artForm.name.first().toString(), fontSize = 80.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
                
                // Shadow overlay for the title area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))))
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = KarnatakaYellow.copy(alpha = 0.2f)) {
                        Text(
                            text = "📍 ${artForm.region}",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = KarnatakaRed
                        )
                    }
                    if (artForm.isAiGenerated) {
                        Spacer(Modifier.width(12.dp))
                        AIGeneratedBadge()
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                
                Text(
                    text = artForm.summary,
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary,
                    lineHeight = 24.sp
                )
                
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = KarnatakaRed.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(Modifier.height(20.dp))

                if (isGenerating) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = KarnatakaYellow, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Gemini is generating a heritage description...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = artForm.description.ifBlank { "No description available yet. Stay tuned as we document this heritage form." },
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Justify,
                        lineHeight = 28.sp
                    )
                }

                if (artForm.youtubeVideoId.isNotBlank()) {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        text = "Watch Experience",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = KarnatakaRed
                    )
                    Spacer(Modifier.height(12.dp))
                    YoutubeThumbnailPlayer(videoId = artForm.youtubeVideoId.trim())
                }
                
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun YoutubeThumbnailPlayer(videoId: String) {
    val context = LocalContext.current

    // YouTube provides free thumbnail URLs — no API key needed.
    // hqdefault = 480x360, mqdefault = 320x180, maxresdefault = 1280x720 (not always available)
    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    val watchUrl    = "https://www.youtube.com/watch?v=$videoId"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                // Try YouTube app first; falls back to browser if not installed
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)).apply {
                    setPackage("com.google.android.youtube")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(watchUrl)))
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Thumbnail image via Coil (already in most Compose projects)
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = "Video thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark scrim so the play button stands out
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Play button
        Icon(
            imageVector = Icons.Filled.PlayCircle,
            contentDescription = "Play video",
            tint = Color.White,
            modifier = Modifier.size(72.dp)
        )
    }
}