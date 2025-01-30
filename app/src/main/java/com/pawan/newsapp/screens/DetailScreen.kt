package com.pawan.newsapp.screens

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.pawan.newsapp.R
import com.pawan.newsapp.viewmodel.Article
import com.pawan.newsapp.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    articleTitle: String,
    articleDescription: String,
    articleImageUrl: String,
    articleUrl: String,
    favoritesViewModel: FavoritesViewModel
) {
    val article = Article(articleTitle, articleDescription, articleImageUrl, articleUrl)

    // Check if article is in the favorites list
    val isFavorite = favoritesViewModel.favoritesList.contains(article)

    // Use this value to manage bookmark button state
    var imageRes by remember { mutableStateOf(if (isFavorite) R.drawable.bookmarkselect else R.drawable.unselectbookmark) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Toggle the favorite state
                        if (imageRes == R.drawable.bookmarkselect) {
                            favoritesViewModel.removeFromFavorites(article)
                            imageRes = R.drawable.unselectbookmark
                        } else {
                            favoritesViewModel.addToFavorites(article)
                            imageRes = R.drawable.bookmarkselect
                        }
                    }) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Action Image",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(articleImageUrl),
                contentDescription = "News Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = articleTitle,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trending • 6 hours ago",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = articleDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl))
                    navController.context.startActivity(intent)
                }) {
                    Text("Read Full Article")
                }
            }
        }
    }
}



