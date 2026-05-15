package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.R
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.KarnatakaRed
import com.example.karunada_kala.ui.theme.KarnatakaYellow
import com.example.karunada_kala.ui.theme.Parchment
import com.example.karunada_kala.ui.theme.TextSecondary

@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = { KalaTopBar("About Karunada Kala", showBack = true, onBackClick = onBackClick) },
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
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Karunada Kala",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = KarnatakaRed
            )
            Text(
                "Preserving Karnataka's Living Heritage",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(Modifier.height(32.dp))

            AboutSection(
                title = "Our Mission",
                description = "Karunada Kala is dedicated to bridging the gap between traditional Karnataka artisans and the modern world. Our platform aims to provide a digital home for centuries-old art forms, ensuring they are not only remembered but also thrive in the digital age."
            )

            AboutSection(
                title = "What We Offer",
                description = "From the rhythmic beats of Yakshagana to the intricate weaves of Ilkal, we bring you closer to the heart of Karnataka. Discover authentic art forms, locate artisans on our interactive map, participate in hands-on workshops, and stay updated with cultural events happening across the state."
            )

            AboutSection(
                title = "AI-Powered Discovery",
                description = "Leveraging the power of Google Gemini AI, we ensure that every art form has a vivid and educational description, making cultural knowledge accessible to everyone, everywhere."
            )

            Spacer(Modifier.height(40.dp))
            HorizontalDivider(color = KarnatakaRed.copy(alpha = 0.2f))
            Spacer(Modifier.height(16.dp))
            
            Text(
                "Version 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                "Made with ❤️ for Karnataka",
                style = MaterialTheme.typography.labelSmall,
                color = KarnatakaRed,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun AboutSection(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = KarnatakaRed
        )
        Spacer(Modifier.height(8.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify,
            lineHeight = 24.sp
        )
    }
}
