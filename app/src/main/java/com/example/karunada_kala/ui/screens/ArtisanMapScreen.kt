package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.model.ArtisanType
import com.example.karunada_kala.R
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.navigation.Screen
import com.example.karunada_kala.ui.theme.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

import com.example.karunada_kala.data.KalaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanMapScreen(
    navController: NavController,
    onArtisanClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: KalaViewModel
) {
    var filter          by rememberSaveable { mutableStateOf("All") }
    var selectedArtisan by remember { mutableStateOf<Artisan?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val artisans by viewModel.artisans.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val mapCenter = remember { GeoPoint(15.3173, 75.7139) }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout(onLogout)
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    val filteredArtisans = artisans.filter {
        when (filter) {
            "Workshops"    -> it.type == ArtisanType.WORKSHOP
            "Performances" -> it.type == ArtisanType.PERFORMANCE
            else           -> true
        }
    }

    Scaffold(
        topBar = { KalaTopBar("Artisan Map", onProfileClick = onProfileClick) },
        bottomBar = { KalaBottomBar(Screen.ArtisanMap.route) { r -> navController.navigate(r) { launchSingleTop = true; popUpTo(Screen.Home.route) } } },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mapViewInstance?.controller?.animateTo(mapCenter) },
                containerColor = KarnatakaRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.MyLocation, "Center Map")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm", android.content.Context.MODE_PRIVATE))
                        Configuration.getInstance().userAgentValue = ctx.packageName
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(7.5)
                            controller.setCenter(mapCenter)
                            mapViewInstance = this
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.clear()
                        filteredArtisans.forEach { artisan ->
                            val marker = Marker(mapView).apply {
                                position = GeoPoint(artisan.latitude, artisan.longitude)
                                title    = artisan.name
                                snippet  = "${artisan.artForms.firstOrNull() ?: ""} · ${artisan.district}"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                
                                // PRD §5.2: Distinct map pin icons
                                icon = if (artisan.type == ArtisanType.WORKSHOP) {
                                    mapView.context.getDrawable(R.drawable.ic_workshop)
                                } else {
                                    mapView.context.getDrawable(R.drawable.ic_performance)
                                }

                                setOnMarkerClickListener { _, _ -> selectedArtisan = artisan; true }
                            }
                            mapView.overlays.add(marker)
                        }
                        mapView.invalidate()
                    }
                )

                // Filter chips
                Row(modifier = Modifier.align(Alignment.TopStart).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Workshops", "Performances").forEach { f ->
                        KalaFilterChip(f, filter == f) { filter = f }
                    }
                }

                // Bottom sheet on pin tap
                selectedArtisan?.let { artisan ->
                    Card(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (artisan.imageUrl.isNotBlank()) {
                                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))) {
                                    SubcomposeAsyncImage(
                                        model = artisan.imageUrl,
                                        contentDescription = artisan.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(20.dp)) } }
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(artisan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(artisan.artForms.joinToString(", "), color = TextSecondary)
                                Text("📍 ${artisan.district}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(8.dp))
                                KalaPrimaryButton("View Profile") { onArtisanClick(artisan.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}
