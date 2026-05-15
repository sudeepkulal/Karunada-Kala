package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.R
import com.example.karunada_kala.ui.components.KalaPrimaryButton
import com.example.karunada_kala.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.model.UserRole
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateRegister: () -> Unit,
    viewModel: KalaViewModel = viewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var emailErr by remember { mutableStateOf("") }
    var passErr  by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginErr  by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    fun validate(): Boolean {
        emailErr = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Enter a valid email" else ""
        passErr  = if (password.length < 6) "Minimum 6 characters" else ""
        return emailErr.isEmpty() && passErr.isEmpty()
    }

    fun handleLogin() {
        if (!validate()) return
        isLoading = true
        loginErr = ""
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    viewModel.refreshData() // This now reloads everything including profile
                    
                    // Direct fetch just to get the role for routing
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val roleStr = doc.getString("role") ?: "EXPLORER"
                            val role = try { UserRole.valueOf(roleStr) } catch(e: Exception) { UserRole.EXPLORER }
                            isLoading = false
                            onLoginSuccess(role)
                        }
                        .addOnFailureListener {
                            isLoading = false
                            loginErr = "Profile load failed"
                        }
                }
            }
            .addOnFailureListener {
                isLoading = false
                loginErr = it.localizedMessage ?: "Login failed"
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Parchment).verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(80.dp)
        )
        Text(stringResource(R.string.login_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = KarnatakaRed)
        Text(stringResource(R.string.login_subtitle), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(40.dp))

        if (loginErr.isNotEmpty()) {
            Text(loginErr, color = ErrorRed, modifier = Modifier.padding(bottom = 16.dp))
        }

        OutlinedTextField(value = email, onValueChange = { email = it; emailErr = "" },
            label = { Text(stringResource(R.string.email_label)) }, leadingIcon = { Icon(Icons.Filled.Email, null) },
            isError = emailErr.isNotEmpty(), supportingText = { if (emailErr.isNotEmpty()) Text(emailErr, color = ErrorRed) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = password, onValueChange = { password = it; passErr = "" },
            label = { Text(stringResource(R.string.password_label)) }, leadingIcon = { Icon(Icons.Filled.Lock, null) },
            trailingIcon = { IconButton(onClick = { showPass = !showPass }) {
                Icon(if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null) } },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passErr.isNotEmpty(), supportingText = { if (passErr.isNotEmpty()) Text(passErr, color = ErrorRed) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { handleLogin() }),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = kalaFieldColors())

        Spacer(Modifier.height(28.dp))
        
        if (isLoading) {
            CircularProgressIndicator(color = KarnatakaRed)
        } else {
            KalaPrimaryButton(stringResource(R.string.login_button)) { handleLogin() }
        }

        Spacer(Modifier.height(20.dp))
        Row {
            Text(stringResource(R.string.register_prompt), color = TextSecondary)
            Text(stringResource(R.string.register_link), color = KarnatakaRed, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateRegister))
        }
    }
}

@Composable
fun kalaFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = KarnatakaRed,
    focusedLabelColor    = KarnatakaRed,
    cursorColor          = KarnatakaRed,
    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
)