package com.pawan.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pawan.newsapp.model.Article
import com.pawan.newsapp.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    // State for articles fetched from the API
    private val _newsState = MutableStateFlow<List<Article>>(emptyList())
    val newsState: StateFlow<List<Article>> = _newsState

    // State for loading status
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // State for error status
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    // State to manage the list of favorite articles
    private val _favoriteArticles = MutableStateFlow<MutableSet<Article>>(mutableSetOf())
    val favoriteArticles: StateFlow<MutableSet<Article>> = _favoriteArticles

    init {
        fetchNews()
    }

    // Fetch news from API
    private fun fetchNews() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isError.value = false

                val response = RetrofitInstance.api.getNews()
                _newsState.value = response.articles

            } catch (e: Exception) {
                e.printStackTrace()
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }


}
