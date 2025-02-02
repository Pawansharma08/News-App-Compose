package com.pawan.newsapp.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.pawan.newsapp.model.Article
import com.pawan.newsapp.notification.sendNewsNotification
import com.pawan.newsapp.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel = viewModel(), navController: NavController) {
    val newsState by viewModel.newsState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val context = LocalContext.current

    // SharedPreferences for notification settings
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var isNotificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true)) }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Request notification permission on launch
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Trigger notification when new data arrives (if enabled)
    LaunchedEffect(newsState) {
        if (newsState.isNotEmpty() && isNotificationsEnabled) {
            val article = newsState.first()
            sendNewsNotification(context, article.title, article.imageUrl)
        }
    }

    // Save notification setting to SharedPreferences when it changes
    LaunchedEffect(isNotificationsEnabled) {
        sharedPreferences.edit().putBoolean("notifications_enabled", isNotificationsEnabled).apply()
    }

    // Category Dropdown Logic
    val categories = listOf("All", "Business", "Technology", "Sports", "Entertainment", "Health")
    var selectedCategory by remember { mutableStateOf("All") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Filter news by category
    val filteredNews = if (selectedCategory == "All") newsState else newsState.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News App") },
                actions = {
                    Box {
                        Button(onClick = { isDropdownExpanded = true }) {
                            Text(selectedCategory)
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                isError -> {
                    Text("Server Error. Try again later.", style = MaterialTheme.typography.bodyLarge)
                }

                filteredNews.isNotEmpty() -> {
                    LazyColumn {
                        items(filteredNews) { article ->
                            NewsItem(article, navController)
                        }
                    }
                }

                else -> {
                    Text("No news available for this category.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun NewsItem(article: Article, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("detail/${Uri.encode(article.title)}/${Uri.encode(article.description ?: "No description")}/${Uri.encode(article.imageUrl ?: "")}/${Uri.encode(article.url)}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            article.imageUrl?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 2.dp)
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = article.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description ?: "No description available",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
