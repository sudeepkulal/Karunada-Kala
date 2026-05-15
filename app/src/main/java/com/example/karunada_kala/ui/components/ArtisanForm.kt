package com.example.karunada_kala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.karunada_kala.model.ArtisanType
import com.example.karunada_kala.ui.screens.kalaFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanFields(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String = "",
    onPasswordChange: (String) -> Unit = {},
    artForms: String,
    onArtFormsChange: (String) -> Unit,
    district: String,
    onDistrictChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    latitude: String,
    onLatitudeChange: (String) -> Unit,
    longitude: String,
    onLongitudeChange: (String) -> Unit,
    artisanType: ArtisanType,
    onArtisanTypeChange: (ArtisanType) -> Unit,
    imageUrl: String,
    onImageUrlChange: (String) -> Unit,
    showAccountSecurity: Boolean = true,
    showBasicInfo: Boolean = true,
    onPickOnMap: () -> Unit = {}
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var showPass by remember { mutableStateOf(false) }

    // ── 1. Basic Information ─────────────────────────────────────────────────
    if (showBasicInfo) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Filled.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = kalaFieldColors()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Mobile Number") },
            leadingIcon = { Icon(Icons.Filled.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = kalaFieldColors()
        )
        Spacer(Modifier.height(10.dp))
    }

    OutlinedTextField(
        value = district,
        onValueChange = onDistrictChange,
        label = { Text("District") },
        leadingIcon = { Icon(Icons.Filled.LocationOn, null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = kalaFieldColors()
    )
    Spacer(Modifier.height(10.dp))

    OutlinedTextField(
        value = artForms,
        onValueChange = onArtFormsChange,
        label = { Text("Art Forms (e.g. Yakshagana, Pottery)") },
        leadingIcon = { Icon(Icons.Filled.Palette, null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = kalaFieldColors()
    )
    Spacer(Modifier.height(10.dp))

    OutlinedTextField(
        value = bio,
        onValueChange = onBioChange,
        label = { Text("Bio (Experience & History)") },
        leadingIcon = { Icon(Icons.Filled.Description, null) },
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(10.dp),
        colors = kalaFieldColors()
    )
    Spacer(Modifier.height(16.dp))

    // ── 2. Discovery & Map Features ──────────────────────────────────────────
    Text("Discovery & Map Features", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(8.dp))
    
    ExposedDropdownMenuBox(
        expanded = typeExpanded,
        onExpandedChange = { typeExpanded = it }
    ) {
        OutlinedTextField(
            value = artisanType.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Artisan Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(10.dp),
            colors = kalaFieldColors()
        )
        ExposedDropdownMenu(
            expanded = typeExpanded,
            onDismissRequest = { typeExpanded = false }
        ) {
            ArtisanType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onArtisanTypeChange(type)
                        typeExpanded = false
                    }
                )
            }
        }
    }
    Spacer(Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = latitude,
                onValueChange = onLatitudeChange,
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = longitude,
                onValueChange = onLongitudeChange,
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors()
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FilledTonalIconButton(
                onClick = onPickOnMap,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(Icons.Default.Map, contentDescription = "Pick on Map")
            }
            Text("Pick", style = MaterialTheme.typography.labelSmall)
        }
    }
    Spacer(Modifier.height(16.dp))

    // ── 3. Visual Content ────────────────────────────────────────────────────
    Text("Visual Content", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = imageUrl,
        onValueChange = onImageUrlChange,
        label = { Text("Profile Image URL") },
        leadingIcon = { Icon(Icons.Filled.Image, null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = kalaFieldColors()
    )
    Spacer(Modifier.height(16.dp))

    // ── 4. Account Security ──────────────────────────────────────────────────
    if (showAccountSecurity) {
        Text("Account Security", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = kalaFieldColors()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, null) },
            trailingIcon = { IconButton(onClick = { showPass = !showPass }) {
                Icon(if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = kalaFieldColors()
        )
    }
}
