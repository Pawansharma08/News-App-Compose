package com.pawan.newsapp.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.pawan.newsapp.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(favoritesViewModel: FavoritesViewModel, navController: NavController) {
    // Observe the favorites list
    val favoritesList by remember { mutableStateOf(favoritesViewModel.favoritesList) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Favorite Articles", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (favoritesList.isEmpty()) {
            Text("No favorites added yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(favoritesList.filter { it.imageUrl?.isNotEmpty() == true }) { article ->
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(article) {
                        isVisible = true
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // Add the article to favorites if it's not already in the list
                                favoritesViewModel.addToFavorites(article)

                                // Navigate to the detail screen with article parameters
                                navController.navigate(
                                    "detail/${Uri.encode(article.title)}/${Uri.encode(article.description ?: "No description")}/${
                                        Uri.encode(
                                            article.imageUrl ?: ""
                                        )
                                    }/${Uri.encode(article.url)}"
                                )
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        ) {
                            // News Image
                            Image(
                                painter = rememberImagePainter(article.imageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.large),
                                contentScale = ContentScale.Crop
                            )

                            // Title Overlay with Smaller Font & Ellipsis
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomStart)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = article.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge, // Smaller font size
                                    maxLines = 1, // Limit title to one line
                                    overflow = TextOverflow.Ellipsis, // Cut off long titles with "..."
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
        }
    }
}
