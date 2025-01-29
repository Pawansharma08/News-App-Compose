package com.pawan.newsapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "News from all around the world",
                style = MaterialTheme.typography.bodyLarge
            )

            SearchBar(
                query = "",
                onQueryChange = { },
                onSearch = { },
                active = false,
                onActiveChange = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                // Search suggestions
            }

            // Category chips
            ScrollableTabRow(selectedTabIndex = 0) {
                listOf("All", "Politic", "Sport", "Education").forEachIndexed { index, text ->
                    Tab(
                        selected = index == 0,
                        onClick = { },
                        text = { Text(text) }
                    )
                }
            }

            // News items
            LazyColumn {
                // Add news items here
            }
        }
    }
}