package com.pawan.newsapp.model


import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("articles") val articles: List<Article>
)

data class Article(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val imageUrl: String?,
    val category: String = "General" // Default category
)

data class Source(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)
