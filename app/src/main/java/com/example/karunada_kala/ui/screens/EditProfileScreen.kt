package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.ui.components.KalaPrimaryButton
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.*

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: KalaViewModel
) {
    val userProfile by viewModel.currentUserProfile.collectAsState()

    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var phone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = { KalaTopBar("Edit Profile", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            
            Text(
                "Update your personal information",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(24.dp))

            // Email (Read-only)
            OutlinedTextField(
                value = userProfile?.email ?: "",
                onValueChange = {},
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = TextSecondary.copy(alpha = 0.2f),
                    disabledLabelColor = TextSecondary,
                    disabledTextColor = TextSecondary
                )
            )
            
            Spacer(Modifier.height(16.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMsg = "" },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = kalaFieldColors()
            )

            Spacer(Modifier.height(16.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; errorMsg = "" },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                colors = kalaFieldColors()
            )

            if (errorMsg.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(errorMsg, color = ErrorRed, fontSize = 14.sp)
            }

            Spacer(Modifier.height(40.dp))

            if (isLoading) {
                CircularProgressIndicator(color = KarnatakaRed)
            } else {
                KalaPrimaryButton("Save Changes") {
                    if (name.isBlank()) {
                        errorMsg = "Name cannot be empty"
                        return@KalaPrimaryButton
                    }
                    isLoading = true
                    viewModel.updateProfile(
                        name = name.trim(),
                        phone = phone.trim(),
                        onSuccess = {
                            isLoading = false
                            onSuccess()
                        },
                        onError = {
                            isLoading = false
                            errorMsg = it
                        }
                    )
                }
            }
        }
    }
}
