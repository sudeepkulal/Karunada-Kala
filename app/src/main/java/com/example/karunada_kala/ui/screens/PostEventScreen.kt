package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.karunada_kala.ui.components.*
import com.example.karunada_kala.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.KalaEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEventScreen(
    onBackClick: () -> Unit,
    onSubmit: () -> Unit,
    viewModel: KalaViewModel = viewModel()
) {
    var eventName   by remember { mutableStateOf("") }
    var artForm     by remember { mutableStateOf("") }
    var district    by remember { mutableStateOf("") }
    var dateTime    by remember { mutableStateOf("") }
    var organiser   by remember { mutableStateOf("") } // Added organiser
    var imageUrl    by remember { mutableStateOf("") }
    var artExpanded by remember { mutableStateOf(false) }
    var nameErr     by remember { mutableStateOf("") }
    var dateErr     by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val userProfile by viewModel.currentUserProfile.collectAsState()
    val artForms by viewModel.artForms.collectAsState()
    
    // Set default organiser name from profile if not already set
    LaunchedEffect(userProfile) {
        if (organiser.isEmpty() && userProfile != null) {
            organiser = userProfile?.name ?: ""
        }
    }

    val artFormOptions = artForms.map { it.name }

    fun validate(): Boolean {
        nameErr = if (eventName.isBlank()) "Event name is required" else ""
        dateErr = if (dateTime.isBlank()) "Date and time is required" else ""
        val districtErr = if (district.isBlank()) "Location is required" else ""
        val artFormErr = if (artForm.isBlank()) "Select an art form" else ""
        
        if (nameErr.isNotEmpty()) return false
        if (dateErr.isNotEmpty()) return false
        if (districtErr.isNotEmpty()) {
            // Simple approach: show as temporary snackbar or just return false
            return false
        }
        return artFormErr.isEmpty()
    }

    Scaffold(topBar = { KalaTopBar("Post Event", showBack = true, onBackClick = onBackClick) }, containerColor = Parchment) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp)) {

            OutlinedTextField(value = eventName, onValueChange = { eventName = it; nameErr = "" },
                label = { Text("Event Name") }, leadingIcon = { Icon(Icons.Filled.Event, null) },
                isError = nameErr.isNotEmpty(), supportingText = { if (nameErr.isNotEmpty()) Text(nameErr, color = ErrorRed) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
            Spacer(Modifier.height(10.dp))

            ExposedDropdownMenuBox(expanded = artExpanded, onExpandedChange = { artExpanded = it }) {
                OutlinedTextField(value = artForm, onValueChange = {}, label = { Text("Art Form") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(artExpanded) }, readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
                ExposedDropdownMenu(expanded = artExpanded, onDismissRequest = { artExpanded = false }) {
                    artFormOptions.forEach { af -> DropdownMenuItem(text = { Text(af) }, onClick = { artForm = af; artExpanded = false }) }
                }
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = district, onValueChange = { district = it },
                label = { Text("District / Location") }, leadingIcon = { Icon(Icons.Filled.LocationOn, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = dateTime, onValueChange = { dateTime = it; dateErr = "" },
                label = { Text("Date & Time (e.g. 20 Jun 2025, 7:30 PM)") }, leadingIcon = { Icon(Icons.Filled.CalendarMonth, null) },
                isError = dateErr.isNotEmpty(), supportingText = { if (dateErr.isNotEmpty()) Text(dateErr, color = ErrorRed) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = organiser, onValueChange = { organiser = it },
                label = { Text("Organiser Name") }, leadingIcon = { Icon(Icons.Filled.Person, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it },
                label = { Text("Event Image URL") }, leadingIcon = { Icon(Icons.Filled.Image, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())
            Spacer(Modifier.height(28.dp))

            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                KalaPrimaryButton("Post Event") {
                    if (validate()) {
                        isSubmitting = true
                        val newEvent = KalaEvent(
                            name = eventName,
                            artForm = artForm,
                            district = district,
                            dateTime = dateTime,
                            organiser = organiser,
                            organiserId = userProfile?.uid ?: "",
                            imageUrl = imageUrl
                        )
                        viewModel.addEvent(newEvent) {
                            isSubmitting = false
                            onSubmit()
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
