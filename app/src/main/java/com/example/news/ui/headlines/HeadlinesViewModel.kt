package com.example.news.ui.headlines

import androidx.lifecycle.viewModelScope
import com.example.news.appdata.ApplicationData
import com.example.news.core.MviViewModel
import com.example.news.data.Article
import com.example.news.data.NewsApiError
import com.example.news.data.NewsApiRepository
import kotlinx.coroutines.launch

class HeadlinesViewModel(
    private val newsApiRepository: NewsApiRepository,
    private val applicationData: ApplicationData,
) : MviViewModel<HeadlinesIntent, HeadlinesState>(HeadlinesState.Loading) {

    var lastArticles = listOf<Article>()

    override fun processIntent(intent: HeadlinesIntent) {
        when (intent) {
            is HeadlinesIntent.ViewInitialized -> viewModelScope.launch { initializeData() }
            is HeadlinesIntent.ArticleTapped -> lastArticles.getOrNull(intent.index)?.let {
                state.postValue(HeadlinesState.OpenUrl(it.url))
            }
        }
    }

    suspend fun initializeData() {
        applicationData.getAppId()?.let {
            try {
                val result = newsApiRepository.getTopHeadlines(it)
                lastArticles = result.articles
                state.postValue(HeadlinesState.DisplayArticles(result.articles))
            } catch (exception: NewsApiError) {
                state.postValue(HeadlinesState.Error(exception))
            }
        }
    }
}

sealed class HeadlinesIntent {
    object ViewInitialized : HeadlinesIntent()
    data class ArticleTapped(
        val index: Int
    ) : HeadlinesIntent()
}

sealed class HeadlinesState {
    object Loading : HeadlinesState()
    data class DisplayArticles(
        val articles: List<Article>,
    ) : HeadlinesState()
    data class Error(
        val error: NewsApiError
    ) : HeadlinesState()
    data class OpenUrl(
        val url: String,
    ) : HeadlinesState()
}
