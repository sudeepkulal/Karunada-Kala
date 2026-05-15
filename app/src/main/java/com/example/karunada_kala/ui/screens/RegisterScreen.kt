package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.model.UserRole
import com.example.karunada_kala.ui.components.KalaPrimaryButton
import com.example.karunada_kala.ui.theme.*

import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.Artisan
import com.example.karunada_kala.model.ArtisanType
import com.example.karunada_kala.ui.components.ArtisanFields

import com.example.karunada_kala.model.UserProfile
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    navController: androidx.navigation.NavController,
    onRegisterSuccess: () -> Unit,
    onNavigateLogin: () -> Unit,
    viewModel: KalaViewModel
) {
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var role     by remember { mutableStateOf(UserRole.EXPLORER) }
    
    // Artisan Fields
    var district by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    var bio      by remember { mutableStateOf("") }
    var artForms by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var artisanType by remember { mutableStateOf(ArtisanType.WORKSHOP) }
    var imageUrl by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var registerErr by remember { mutableStateOf("") }

    var nameErr  by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf("") }
    var passErr  by remember { mutableStateOf("") }

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

    val auth = FirebaseAuth.getInstance()

    fun validate(): Boolean {
        nameErr  = if (name.isBlank()) "Name is required" else ""
        emailErr = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Valid email required" else ""
        passErr  = if (password.length < 6) "Minimum 6 characters" else ""
        return nameErr.isEmpty() && emailErr.isEmpty() && passErr.isEmpty()
    }

    fun handleRegister() {
        if (!validate()) return
        isSubmitting = true
        registerErr = ""
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val profile = UserProfile(uid = uid, name = name, email = email, phone = phone, role = role)
                // Save profile to Firestore
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users").document(uid).set(profile)
                    .addOnSuccessListener {
                        if (role == UserRole.ARTISAN) {
                            val newArtisan = Artisan(
                                id = uid, // Use Auth UID as Artisan ID
                                name = name,
                                email = email,
                                artForms = artForms.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                district = district,
                                bio = bio,
                                phone = phone,
                                type = artisanType,
                                latitude = latitude.toDoubleOrNull() ?: 0.0,
                                longitude = longitude.toDoubleOrNull() ?: 0.0,
                                imageUrl = imageUrl,
                                productImages = emptyList() // Removed product gallery
                            )
                            viewModel.addArtisan(newArtisan) {
                                viewModel.refreshData()
                                isSubmitting = false
                                onRegisterSuccess()
                            }
                        } else {
                            viewModel.refreshData()
                            isSubmitting = false
                            onRegisterSuccess()
                        }
                    }
                    .addOnFailureListener {
                        isSubmitting = false
                        registerErr = "Profile creation failed"
                    }
            }
            .addOnFailureListener {
                isSubmitting = false
                registerErr = it.localizedMessage ?: "Registration failed"
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Parchment).verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(36.dp))
        Text("Create Account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = KarnatakaRed)
        Text("Join the cultural movement", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(32.dp))

        if (registerErr.isNotEmpty()) {
            Text(registerErr, color = ErrorRed, modifier = Modifier.padding(bottom = 16.dp))
        }

        // Common Account Fields
        OutlinedTextField(value = name, onValueChange = { name = it; nameErr = "" },
            label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Filled.Person, null) },
            isError = nameErr.isNotEmpty(), supportingText = { if (nameErr.isNotEmpty()) Text(nameErr, color = ErrorRed) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = email, onValueChange = { email = it; emailErr = "" },
            label = { Text("Email") }, leadingIcon = { Icon(Icons.Filled.Email, null) },
            isError = emailErr.isNotEmpty(), supportingText = { if (emailErr.isNotEmpty()) Text(emailErr, color = ErrorRed) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = phone, onValueChange = { phone = it },
            label = { Text("Phone Number") }, leadingIcon = { Icon(Icons.Filled.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = password, onValueChange = { password = it; passErr = "" },
            label = { Text("Password") }, leadingIcon = { Icon(Icons.Filled.Lock, null) },
            trailingIcon = { IconButton(onClick = { showPass = !showPass }) {
                Icon(if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passErr.isNotEmpty(), supportingText = { if (passErr.isNotEmpty()) Text(passErr, color = ErrorRed) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (role != UserRole.ARTISAN) handleRegister() }),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
        Spacer(Modifier.height(16.dp))

        Text("I am a …", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { role = UserRole.EXPLORER }, modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (role == UserRole.EXPLORER) KarnatakaRed.copy(alpha = 0.1f) else CardSurface)) {
                Text("🔍 Explorer", color = if (role == UserRole.EXPLORER) KarnatakaRed else TextPrimary, fontWeight = if (role == UserRole.EXPLORER) FontWeight.Bold else FontWeight.Normal)
            }
            OutlinedButton(onClick = { role = UserRole.ARTISAN }, modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (role == UserRole.ARTISAN) KarnatakaRed.copy(alpha = 0.1f) else CardSurface)) {
                Text("🎭 Artisan", color = if (role == UserRole.ARTISAN) KarnatakaRed else TextPrimary, fontWeight = if (role == UserRole.ARTISAN) FontWeight.Bold else FontWeight.Normal)
            }
        }
        Spacer(Modifier.height(16.dp))

        if (role == UserRole.ARTISAN) {
            ArtisanFields(
                name = name,
                onNameChange = { name = it; nameErr = "" },
                email = email,
                onEmailChange = { email = it; emailErr = "" },
                password = password,
                onPasswordChange = { password = it; passErr = "" },
                artForms = artForms,
                onArtFormsChange = { artForms = it },
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
                showAccountSecurity = false,
                showBasicInfo = false, // We already showed name, email, phone above
                onPickOnMap = {
                    navController.navigate(
                        com.example.karunada_kala.ui.navigation.Screen.LocationPicker.createRoute(
                            latitude.toDoubleOrNull(),
                            longitude.toDoubleOrNull()
                        )
                    )
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(12.dp))
        if (isSubmitting) {
            CircularProgressIndicator(color = KarnatakaRed)
        } else {
            KalaPrimaryButton("Create Account") { handleRegister() }
        }
        Spacer(Modifier.height(16.dp))
        Row {
            Text("Already have an account? ", color = TextSecondary)
            Text("Login", color = KarnatakaRed, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateLogin))
        }
        Spacer(Modifier.height(24.dp))
    }
}