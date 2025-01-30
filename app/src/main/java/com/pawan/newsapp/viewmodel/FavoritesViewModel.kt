package com.pawan.newsapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Article(val title: String, val description: String, val imageUrl: String, val url: String)

class FavoritesViewModel : ViewModel() {
    var favoritesList by mutableStateOf<List<Article>>(emptyList())
        private set

    // Add article to favorites only if it's not already in the list (to avoid duplicates)
    fun addToFavorites(article: Article) {
        // Check if article is already in the favorites list
        if (!favoritesList.contains(article)) {
            favoritesList = favoritesList + article
        }
    }

    // Remove article from favorites
    fun removeFromFavorites(article: Article) {
        favoritesList = favoritesList.filter { it != article }
    }
}
