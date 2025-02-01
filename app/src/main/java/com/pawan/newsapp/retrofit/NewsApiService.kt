package com.pawan.newsapp.retrofit

import com.pawan.newsapp.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

interface NewsApiService {

    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String = "india",
        @Query("from") from: String = getYesterdayDate(),
        @Query("to") to: String = getYesterdayDate(),
        @Query("sortBy") sortBy: String = "popularity",
        @Query("apiKey") apiKey: String = "f5e7b2c8af3b4df6bd59aa8ac06c1cf0"
    ): NewsResponse
}

fun getYesterdayDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1) // Subtract 1 day
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}
