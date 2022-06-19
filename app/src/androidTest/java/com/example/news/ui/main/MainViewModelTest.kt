package com.example.news.ui.main

import com.example.news.appdata.ApplicationData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private const val APP_ID = "app_id"

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @Test
    fun onCreatedWithoutAppId_appIdPromptDisplayed() {
        val applicationData = mock<ApplicationData> {
            on { getAppId() } doReturn null
        }

        val viewModel = MainViewModel(applicationData)

        assert(viewModel.state().value == MainState.PromptForId)
    }

    @Test
    fun onCreatedWithAppId_headlinesDisplayed() {
        val applicationData = mock<ApplicationData> {
            on { getAppId() } doReturn APP_ID
        }

        val viewModel = MainViewModel(applicationData)

        assert(viewModel.state().value == MainState.HeadlinesTab)
    }

    @Test
    fun onHeadlinesTapped_headlinesSetToDisplay() {
        val applicationData = mock<ApplicationData> {
            on { getAppId() } doReturn APP_ID
        }

        val viewModel = MainViewModel(applicationData)
        viewModel.processIntent(MainIntent.OnHeadlinesSelected)

        assert(viewModel.state().value == MainState.HeadlinesTab)
    }

    @Test
    fun onSearchTapped_searchSetToDisplay() = runTest {
        val applicationData = mock<ApplicationData> {
            on { getAppId() } doReturn APP_ID
        }

        val viewModel = MainViewModel(applicationData)
        viewModel.processIntent(MainIntent.OnSearchSelected)

        launch { advanceUntilIdle() }

        assert(viewModel.state().value == MainState.SearchTab)
    }
}
