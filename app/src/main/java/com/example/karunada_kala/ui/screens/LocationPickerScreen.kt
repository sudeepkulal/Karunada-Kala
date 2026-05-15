package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.KarnatakaRed
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun LocationPickerScreen(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onLocationPicked: (Double, Double) -> Unit,
    onBackClick: () -> Unit
) {
    var pickedLocation by remember { 
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null) 
                GeoPoint(initialLatitude, initialLongitude) 
            else 
                GeoPoint(15.3173, 75.7139) // Default Karnataka Center
        ) 
    }

    Scaffold(
        topBar = { KalaTopBar("Pick Location", showBack = true, onBackClick = onBackClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onLocationPicked(pickedLocation.latitude, pickedLocation.longitude) },
                containerColor = KarnatakaRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirm Location")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm", android.content.Context.MODE_PRIVATE))
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(8.0)
                        controller.setCenter(pickedLocation)

                        val marker = Marker(this).apply {
                            position = pickedLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        overlays.add(marker)

                        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    pickedLocation = it
                                    marker.position = it
                                    invalidate()
                                }
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                p?.let {
                                    pickedLocation = it
                                    marker.position = it
                                    invalidate()
                                }
                                return true
                            }
                        })
                        overlays.add(eventsOverlay)
                    }
                },
                update = { mapView ->
                    // Handle updates if needed
                }
            )
            
            Card(
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Text(
                    "Tap on the map to set the artisan's location",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
