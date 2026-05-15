package com.example.karunada_kala.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.R
import com.example.karunada_kala.ui.theme.KarnatakaRed
import com.example.karunada_kala.ui.theme.KarnatakaYellow
import com.example.karunada_kala.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: (UserRole?) -> Unit) {
    val alpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(1000), label = "alpha")
    
    LaunchedEffect(Unit) {
        delay(2200)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val roleStr = doc.getString("role") ?: "EXPLORER"
                        val role = try { UserRole.valueOf(roleStr) } catch(e: Exception) { UserRole.EXPLORER }
                        onNavigateToLogin(role)
                    } else {
                        // User exists in Auth but not in Firestore - force logout
                        FirebaseAuth.getInstance().signOut()
                        onNavigateToLogin(null)
                    }
                }
                .addOnFailureListener {
                    onNavigateToLogin(null)
                }
        } else {
            onNavigateToLogin(null)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(KarnatakaRed), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(alpha)) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Karunada Kala", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))
            Text("ಕರ್ನಾಟಕದ ಕಲಾ ಸಂಪ್ರದಾಯ", fontSize = 16.sp, color = KarnatakaYellow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Text("Discover · Preserve · Celebrate", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.5.sp)
        }
    }
}