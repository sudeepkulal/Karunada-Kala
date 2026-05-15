package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.model.ArtisanType
import com.example.karunada_kala.ui.components.ArtisanFields
import com.example.karunada_kala.ui.components.KalaPrimaryButton
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.Parchment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddArtisanScreen(
    navController: androidx.navigation.NavController,
    artisanId: String? = null,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: KalaViewModel = viewModel()
) {
    val artisans by viewModel.artisans.collectAsState()
    val existingArtisan = remember(artisanId, artisans) {
        artisans.find { it.id == artisanId }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var artFormsInput by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var artisanType by remember { mutableStateOf(ArtisanType.WORKSHOP) }
    var imageUrl by remember { mutableStateOf("") }
    var productImagesInput by remember { mutableStateOf("") }
    
    var isSubmitting by remember { mutableStateOf(false) }

    // Listen for results from LocationPicker
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<Double>("picked_lat", 0.0)?.collect {
            if (it != 0.0) latitude = it.toString()
        }
    }
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<Double>("picked_lng", 0.0)?.collect {
            if (it != 0.0) longitude = it.toString()
        }
    }

    LaunchedEffect(existingArtisan) {
        existingArtisan?.let {
            name = it.name
            email = it.email
            artFormsInput = it.artForms.joinToString(", ")
            district = it.district
            bio = it.bio
            phone = it.phone
            latitude = it.latitude.toString()
            longitude = it.longitude.toString()
            artisanType = it.type
            imageUrl = it.imageUrl
            productImagesInput = it.productImages.joinToString(", ")
        }
    }

    Scaffold(
        topBar = { KalaTopBar(if (artisanId == null) "Add Artisan" else "Edit Artisan", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            ArtisanFields(
                name = name,
                onNameChange = { name = it },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                artForms = artFormsInput,
                onArtFormsChange = { artFormsInput = it },
                district = district,
                onDistrictChange = { district = it },
                bio = bio,
                onBioChange = { bio = it },
                phone = phone,
                onPhoneChange = { phone = it },
                latitude = latitude,
                onLatitudeChange = { latitude = it },
                longitude = longitude,
                onLongitudeChange = { longitude = it },
                artisanType = artisanType,
                onArtisanTypeChange = { artisanType = it },
                imageUrl = imageUrl,
                onImageUrlChange = { imageUrl = it },
                productImages = productImagesInput,
                onProductImagesChange = { productImagesInput = it },
                showAccountSecurity = artisanId == null, // Only show security fields when adding new
                onPickOnMap = {
                    navController.navigate(
                        com.example.karunada_kala.ui.navigation.Screen.LocationPicker.createRoute(
                            latitude.toDoubleOrNull(),
                            longitude.toDoubleOrNull()
                        )
                    )
                }
            )

            Spacer(Modifier.height(28.dp))

            if (isSubmitting) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                KalaPrimaryButton(if (artisanId == null) "Save Artisan" else "Update Artisan") {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        isSubmitting = true
                        val artisan = Artisan(
                            id = artisanId ?: "",
                            name = name,
                            email = email,
                            artForms = artFormsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            district = district,
                            bio = bio,
                            phone = phone,
                            latitude = latitude.toDoubleOrNull() ?: 0.0,
                            longitude = longitude.toDoubleOrNull() ?: 0.0,
                            type = artisanType,
                            imageUrl = imageUrl,
                            productImages = productImagesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                        viewModel.addArtisan(artisan) {
                            isSubmitting = false
                            onSuccess()
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
