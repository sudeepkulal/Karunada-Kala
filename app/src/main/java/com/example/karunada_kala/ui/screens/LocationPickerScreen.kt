package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.KarnatakaRed
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import android.location.Location
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun LocationPickerScreen(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onLocationPicked: (Double, Double) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }
    var markerInstance by remember { mutableStateOf<Marker?>(null) }

    var pickedLocation by remember { 
        mutableStateOf(
            if (initialLatitude != null && initialLongitude != null && initialLatitude != 0.0) 
                GeoPoint(initialLatitude, initialLongitude) 
            else 
                GeoPoint(15.3173, 75.7139) // Default Karnataka Center
        ) 
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Logic to move to current location will be called again or handled here
        }
    }

    fun moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    pickedLocation = geoPoint
                    mapViewInstance?.let { mv ->
                        mv.controller.animateTo(geoPoint)
                        mv.controller.setZoom(15.0)
                        markerInstance?.position = geoPoint
                        mv.invalidate()
                    }
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = { KalaTopBar("Pick Location", showBack = true, onBackClick = onBackClick) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = { moveToCurrentLocation() },
                    containerColor = Color.White,
                    contentColor = KarnatakaRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                }
                
                FloatingActionButton(
                    onClick = { onLocationPicked(pickedLocation.latitude, pickedLocation.longitude) },
                    containerColor = KarnatakaRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirm Location")
                }
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
                        mapViewInstance = this

                        val marker = Marker(this).apply {
                            position = pickedLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        markerInstance = marker
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
