package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.WorkshopRegistration
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.theme.*

/**
 * PRD §5.4 Workshop Sign-Up
 * Captures registration details and saves them to Firestore 'registrations' collection.
 *
 * @param artisanId   Firestore document ID of the artisan being signed up with
 * @param artisanName Display name of the artisan (shown at top of form)
 * @param artForms    List of art forms offered by the artisan (pre-populates dropdown)
 * @param onBackClick Navigate back
 * @param onSubmitSuccess Navigate to Confirmation screen
 * @param viewModel   Shared KalaViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopSignUpScreen(
    artisanId: String = "",
    artisanName: String,
    artForms: List<String>,
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit,
    viewModel: KalaViewModel
) {
    val userProfile by viewModel.currentUserProfile.collectAsState()

    var fullName      by remember { mutableStateOf("") }
    var phone         by remember { mutableStateOf("") }
    var email         by remember { mutableStateOf("") }
    var artForm       by remember { mutableStateOf(artForms.firstOrNull() ?: "") }
    var preferredDate by remember { mutableStateOf("") }
    var message       by remember { mutableStateOf("") }
    var expanded      by remember { mutableStateOf(false) }
    var nameErr       by remember { mutableStateOf("") }
    var phoneErr      by remember { mutableStateOf("") }
    var dateErr       by remember { mutableStateOf("") }
    var isSubmitting  by remember { mutableStateOf(false) }
    var submitError   by remember { mutableStateOf("") }

    // Pre-fill fields if user is logged in
    LaunchedEffect(userProfile) {
        userProfile?.let {
            if (fullName.isEmpty()) fullName = it.name
            if (email.isEmpty()) email = it.email
            if (phone.isEmpty()) phone = it.phone
        }
    }

    val allArtForms = artForms

    fun validate(): Boolean {
        nameErr  = if (fullName.isBlank()) "Name is required" else ""
        phoneErr = if (phone.length < 10) "Enter a valid 10-digit phone number" else ""
        dateErr  = if (preferredDate.isBlank()) "Preferred date is required" else ""
        return nameErr.isEmpty() && phoneErr.isEmpty() && dateErr.isEmpty()
    }

    fun handleSubmit() {
        if (!validate()) return
        isSubmitting = true
        submitError = ""

        val registration = WorkshopRegistration(
            fullName      = fullName.trim(),
            userId        = userProfile?.uid ?: "",
            email         = email.trim(),
            phone         = phone.trim(),
            artForm       = artForm,
            preferredDate = preferredDate.trim(),
            message       = message.trim(),
            artisanName   = artisanName,
            artisanId     = artisanId
        )

        viewModel.submitWorkshopRegistration(
            registration  = registration,
            onSuccess     = {
                isSubmitting = false
                onSubmitSuccess()
            },
            onError       = { err ->
                isSubmitting = false
                submitError = err
            }
        )
    }

    Scaffold(
        topBar = { KalaTopBar("Workshop Sign-Up", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (artisanName.isNotBlank()) {
                Text(
                    "Registering with $artisanName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
            }

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; nameErr = "" },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Filled.Person, null) },
                isError = nameErr.isNotEmpty(),
                supportingText = { if (nameErr.isNotEmpty()) Text(nameErr, color = ErrorRed) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors()
            )
            Spacer(Modifier.height(10.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Filled.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(10.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; phoneErr = "" },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Filled.Phone, null) },
                isError = phoneErr.isNotEmpty(),
                supportingText = { if (phoneErr.isNotEmpty()) Text(phoneErr, color = ErrorRed) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors()
            )
            Spacer(Modifier.height(10.dp))

            // Art Form dropdown
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = artForm,
                    onValueChange = {},
                    label = { Text("Art Form of Interest") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = kalaFieldColors()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    allArtForms.forEach { af ->
                        DropdownMenuItem(text = { Text(af) }, onClick = { artForm = af; expanded = false })
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            // Preferred Date
            OutlinedTextField(
                value = preferredDate,
                onValueChange = { preferredDate = it; dateErr = "" },
                label = { Text("Preferred Date (DD/MM/YYYY)") },
                leadingIcon = { Icon(Icons.Filled.CalendarMonth, null) },
                isError = dateErr.isNotEmpty(),
                supportingText = { if (dateErr.isNotEmpty()) Text(dateErr, color = ErrorRed) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors()
            )
            Spacer(Modifier.height(10.dp))

            // Optional message
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message to Artisan (optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(10.dp),
                colors = kalaFieldColors(),
                maxLines = 4
            )
            Spacer(Modifier.height(12.dp))

            // Error banner
            if (submitError.isNotEmpty()) {
                Surface(
                    color = ErrorRed.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = submitError,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Submit button
            if (isSubmitting) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KarnatakaRed)
                }
            } else {
                KalaPrimaryButton("Submit Registration") { handleSubmit() }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}