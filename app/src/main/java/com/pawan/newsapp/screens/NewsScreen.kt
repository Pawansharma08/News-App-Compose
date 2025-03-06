package com.pawan.newsapp.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.pawan.newsapp.model.Article
import com.pawan.newsapp.notification.sendNewsNotification
import com.pawan.newsapp.viewmodel.NewsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel = viewModel(), navController: NavController) {
    val newsState by viewModel.newsState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isError by viewModel.isError.collectAsState()
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var isNotificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true)) }

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

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(newsState) {
        if (newsState.isNotEmpty() && isNotificationsEnabled) {
            val article = newsState.first()
            sendNewsNotification(context, article.title, article.imageUrl)
        }
    }

    LaunchedEffect(isNotificationsEnabled) {
        sharedPreferences.edit().putBoolean("notifications_enabled", isNotificationsEnabled).apply()
    }

    val categories = listOf("All", "Business", "Technology", "Sports", "Entertainment", "Health")
    var selectedCategory by remember { mutableStateOf("All") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val filteredNews = if (selectedCategory == "All") newsState else newsState.filter { it.category == selectedCategory }

    var searchQuery by remember { mutableStateOf("") }

    val searchedNews = if (searchQuery.isEmpty()) filteredNews else filteredNews.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News App") },
                actions = {
                    Box(
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            )

            if (searchedNews.isNotEmpty()) {
//                NewsSlideshow(searchedNews)
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                isError -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Server Error. Try again later.", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                searchedNews.isNotEmpty() -> {
                    LazyColumn {
                        items(searchedNews) { article ->
                            NewsItem(article, navController)
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No news available for this category.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

//@Composable
//fun NewsSlideshow(articles: List<Article>) {
//    val pagerState = rememberPagerState{3}
//
//    // Auto-slide every 3 seconds
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(3000) // Auto-slide every 3 seconds
//            val nextPage = (pagerState.currentPage + 1) % articles.size
//            pagerState.animateScrollToPage(nextPage)
//        }
//    }
//
//    HorizontalPager(
//        state = pagerState,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(250.dp)
//    ) { page ->
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            // Display the image in the carousel
//            Image(
//                painter = rememberImagePainter(articles[page].imageUrl),
//                contentDescription = null,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//        }
//    }
//}



@Composable
fun NewsItem(article: Article, navController: NavController) {
    // If the imageUrl is empty or null, return early and don't display the item
    if (article.imageUrl.isNullOrEmpty()) return

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    "detail/${Uri.encode(article.title)}/${Uri.encode(article.description ?: "No description")}/${Uri.encode(article.imageUrl ?: "")}/${Uri.encode(article.url)}"
                )
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Image(
                    painter = rememberImagePainter(article.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = article.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
            }

            AnimatedVisibility(visible = isVisible) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = article.description ?: "No description available",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4, // Increased from 2 to 4 for longer descriptions
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}



