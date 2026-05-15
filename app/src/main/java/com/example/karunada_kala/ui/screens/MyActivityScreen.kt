package com.example.karunada_kala.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.karunada_kala.data.KalaViewModel
import com.example.karunada_kala.ui.components.KalaTopBar
import com.example.karunada_kala.ui.theme.Parchment
import com.example.karunada_kala.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyActivityScreen(onBackClick: () -> Unit, viewModel: KalaViewModel) {
    val myRegistrations by viewModel.myRegistrations.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyRegistrations()
    }

    Scaffold(
        topBar = { KalaTopBar("My Activity", showBack = true, onBackClick = onBackClick) },
        containerColor = Parchment
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchMyRegistrations() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (myRegistrations.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No activity yet.", color = TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        Text("Sign up for workshops to see them here!", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Workshop History", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                    }
                    items(myRegistrations.sortedByDescending { it.submittedAt }) { reg ->
                        RegistrationCard(reg)
                    }
                }
            }
        }
    }
}
