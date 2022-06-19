package com.example.news.ui.search

import androidx.lifecycle.viewModelScope
import com.example.news.appdata.ApplicationData
import com.example.news.core.MviViewModel
import com.example.news.data.Article
import com.example.news.data.NewsApiError
import com.example.news.data.NewsApiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val SEARCH_DEBOUNCE_MS = 300

class SearchViewModel(
    private val newsApiRepository: NewsApiRepository,
    private val applicationData: ApplicationData,
) : MviViewModel<SearchIntent, SearchState>(SearchState.DisplayArticles(emptyList())) {

    private val searchUpdates: Channel<String> = Channel(Channel.RENDEZVOUS)
    private var articles = listOf<Article>()

    init {
        searchUpdates
            .consumeAsFlow()
            .filter { it.isNotEmpty() }
            .debounce(SEARCH_DEBOUNCE_MS.milliseconds)
            .onEach {
                performSearch(it)
            }
            .launchIn(viewModelScope)
    }

    override fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SearchUpdated -> viewModelScope.launch { searchUpdates.send(intent.term) }
            is SearchIntent.ArticleTapped -> articles.getOrNull(intent.index)?.let {
                state.postValue(SearchState.OpenUrl(it.url))
            }
        }
    }

    private fun performSearch(term: String) {
        viewModelScope.launch {
            state.postValue(SearchState.Loading)
            applicationData.getAppId()?.let {
                try {
                    val result = newsApiRepository.search(it, term)
                    articles = result.articles
                    state.postValue(SearchState.DisplayArticles(result.articles))
                } catch (exception: NewsApiError) {
                    state.postValue(SearchState.Error(exception))
                }
            }
        }
    }
}

sealed class SearchIntent {
    data class SearchUpdated(val term: String) : SearchIntent()
    data class ArticleTapped(val index: Int) : SearchIntent()
}

sealed class SearchState {
    object Loading : SearchState()
    data class DisplayArticles(val articles: List<Article>) : SearchState()
    data class Error(val error: NewsApiError) : SearchState()
    data class OpenUrl(val url: String) : SearchState()
}
