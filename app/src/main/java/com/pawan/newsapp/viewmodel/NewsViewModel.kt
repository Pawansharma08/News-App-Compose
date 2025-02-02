package com.pawan.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.newsapp.model.Article
import com.pawan.newsapp.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    // State for articles fetched from API
    private val _newsState = MutableStateFlow<List<Article>>(emptyList())
    val newsState: StateFlow<List<Article>> = _newsState

    // State for loading status
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State for error status
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    // Favorite articles
    private val _favoriteArticles = MutableStateFlow<List<Article>>(emptyList())
    val favoriteArticles: StateFlow<List<Article>> = _favoriteArticles

    init {
        fetchNews()
    }

    // Fetch news from API and assign categories
    private fun fetchNews() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isError.value = false

                val response = RetrofitInstance.api.getNews()
                val articles = response.articles.map { article ->
                    article.copy(category = detectCategory(article.title, article.description))
                }

                _newsState.value = articles
            } catch (e: Exception) {
                e.printStackTrace()
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Detect category based on keywords in title & description
    private fun detectCategory(title: String, description: String?): String {
        val text = "$title ${description ?: ""}".lowercase()

        return when {
            "cricket" in text || "football" in text || "match" in text -> "Sports"
            "stocks" in text || "market" in text || "economy" in text -> "Business"
            "ai" in text || "tech" in text || "innovation" in text -> "Technology"
            "health" in text || "medical" in text || "covid" in text -> "Health"
            "movie" in text || "bollywood" in text || "hollywood" in text -> "Entertainment"
            else -> "General"
        }
    }

    // Add article to favorites
    fun addToFavorites(article: Article) {
        val updatedFavorites = _favoriteArticles.value.toMutableList()
        if (!updatedFavorites.contains(article)) {
            updatedFavorites.add(article)
            _favoriteArticles.value = updatedFavorites
        }
    }

    // Remove article from favorites
    fun removeFromFavorites(article: Article) {
        val updatedFavorites = _favoriteArticles.value.toMutableList()
        updatedFavorites.remove(article)
        _favoriteArticles.value = updatedFavorites
    }
}
