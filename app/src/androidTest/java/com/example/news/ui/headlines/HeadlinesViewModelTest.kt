package com.example.news.ui.headlines

import com.example.news.appdata.AndroidApplicationData
import com.example.news.data.Article
import com.example.news.data.HeadlineResponse
import com.example.news.data.NewsApiError
import com.example.news.data.NewsApiRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock

private const val APP_ID = "fake_app_id"
private const val ARTICLE_URL = "url"

@ExperimentalCoroutinesApi
class HeadlinesViewModelTest {

    private val applicationData = mock<AndroidApplicationData> {
        on { getAppId() } doReturn APP_ID
    }

    @Test
    fun onCreate_MakesNetworkRequestAndEmitsState() = runTest {
        val returnedArticles = listOf(mock<Article>())
        val newsApiRepository = mock<NewsApiRepository> {
            onBlocking { getTopHeadlines(APP_ID) } doReturn HeadlineResponse(
                "ok",
                1,
                returnedArticles
            )
        }

        val viewModel = HeadlinesViewModel(newsApiRepository, applicationData)
        viewModel.initializeData()

        launch { advanceUntilIdle() }

        assert(viewModel.state().value == HeadlinesState.DisplayArticles(returnedArticles))
    }

    @Test
    fun onCreate_makesNetworkRequestWithErrorEmitsErrorState() = runTest {
        val result = mock<NewsApiError>()
        val newsApiRepository = mock<NewsApiRepository> {
            onBlocking { getTopHeadlines(APP_ID) } doThrow result
        }

        val viewModel = HeadlinesViewModel(newsApiRepository, applicationData)
        viewModel.initializeData()
        launch { advanceUntilIdle() }

        assert(viewModel.state().value == HeadlinesState.Error(result))
    }

    @Test
    fun onArticleTapped_opensUrl() = runTest {
        val result = listOf(
            mock<Article> {
                on { url } doReturn ARTICLE_URL
            }
        )
        val newsApiRepository = mock<NewsApiRepository> {
            onBlocking { getTopHeadlines(APP_ID) } doReturn HeadlineResponse("ok", 1, result)
        }

        val viewModel = HeadlinesViewModel(newsApiRepository, applicationData)
        viewModel.initializeData()
        launch { advanceUntilIdle() }

        viewModel.processIntent(HeadlinesIntent.ArticleTapped(0))
        launch { advanceUntilIdle() }

        assert(viewModel.state().value == HeadlinesState.OpenUrl(ARTICLE_URL))
    }
}
