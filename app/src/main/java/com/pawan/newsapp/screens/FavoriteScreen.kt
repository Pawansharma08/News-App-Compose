package com.pawan.newsapp.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
                items(favoritesList) { article ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // Add the article to favorites if it's not already in the list
                                favoritesViewModel.addToFavorites(article)

                                // Navigate to the detail screen with article parameters
                                navController.navigate(
                                    "detail/${Uri.encode(article.title)}/${Uri.encode(article.description ?: "No description")}/${Uri.encode(article.imageUrl ?: "")}/${Uri.encode(article.url)}"
                                )
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
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
        }
    }
}
