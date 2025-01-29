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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, articleTitle: String, articleDescription: String, articleImageUrl: String, articleUrl: String) {

    var imageRes by remember { mutableStateOf(R.drawable.unselectbookmark) }

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
                    // IconButton with toggling image functionality
                    IconButton(onClick = {
                        // Toggle between two images
                        imageRes = if (imageRes == R.drawable.bookmarkselect) {
                            R.drawable.unselectbookmark
                        } else {
                            R.drawable.bookmarkselect
                        }
                    }) {
                        Image(
                            painter = painterResource(id = imageRes), // Use the current image resource
                            contentDescription = "Action Image",
                            modifier = Modifier.size(24.dp) // Icon size
                        )
                    }
                    // Second IconButton for More options
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                }            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Load the image dynamically
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
                // Title of the article
                Text(
                    text = articleTitle,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Publish info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trending • 6 hours ago", // Update this with actual data
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Article description
                Text(
                    text = articleDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to open the full article in a browser
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
