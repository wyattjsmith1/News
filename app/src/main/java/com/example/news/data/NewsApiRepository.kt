package com.example.news.data

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiRepository {

    @GET("top-headlines?country=us")
    suspend fun getTopHeadlines(@Query("apiKey") apiKey: String): HeadlineResponse

    @GET("everything")
    suspend fun search(
        @Query("apiKey") apiKey: String,
        @Query("q") term: String
    ): HeadlineResponse
}

data class HeadlineResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>,
)

data class Article(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
)

data class Source(
    val id: String,
    val name: String,
)

data class NewsApiError(
    val status: String,
    val code: String,
    override val message: String,
) : RuntimeException()