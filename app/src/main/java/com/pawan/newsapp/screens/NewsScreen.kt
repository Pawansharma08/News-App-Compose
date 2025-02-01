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

    // SharedPreferences to store the notification state
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

    // Only trigger notification when new data comes, if notifications are enabled
    LaunchedEffect(newsState) {
        if (newsState.isNotEmpty() && isNotificationsEnabled) {
            val article = newsState.first() // Get the first article
            val title = article.title
            val imageUrl = article.imageUrl

            sendNewsNotification(
                context, title, imageUrl // Pass title and imageUrl to the notification function
            )
        }
    }

    // Save notification setting to SharedPreferences when it changes
    LaunchedEffect(isNotificationsEnabled) {
        sharedPreferences.edit().putBoolean("notifications_enabled", isNotificationsEnabled).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("News App") })
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

                newsState.isNotEmpty() -> {
                    LazyColumn {
                        items(newsState) { article ->
                            NewsItem(article, navController)
                        }
                    }
                }

                else -> {
                    Text("No news available.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun NewsItem(article: Article, navController: NavController) {
    val context = LocalContext.current

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
