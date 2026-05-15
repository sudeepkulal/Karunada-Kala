package com.example.karunada_kala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.ui.navigation.KalaNavGraph
import com.example.karunada_kala.ui.theme.KarunadaKalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: KalaViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            KarunadaKalaTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                KalaNavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}