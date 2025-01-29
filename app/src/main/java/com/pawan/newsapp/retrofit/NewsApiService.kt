package com.pawan.newsapp.retrofit

import com.pawan.newsapp.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String = "apple",
        @Query("from") from: String = "2025-01-28",
        @Query("to") to: String = "2025-01-28",
        @Query("sortBy") sortBy: String = "popularity",
        @Query("apiKey") apiKey: String = "f5e7b2c8af3b4df6bd59aa8ac06c1cf0"
    ): NewsResponse
}
