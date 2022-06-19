package com.example.news.ui.main

import com.example.news.appdata.ApplicationData
import com.example.news.core.MviViewModel

class MainViewModel(
    private val appData: ApplicationData,
) : MviViewModel<MainIntent, MainState>(
    if (appData.getAppId() == null) {
        MainState.PromptForId
    } else {
        MainState.HeadlinesTab
    }
) {

    override fun processIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.OnHeadlinesSelected -> state.postValue(MainState.HeadlinesTab)
            is MainIntent.OnSearchSelected -> state.postValue(MainState.SearchTab)
            is MainIntent.OnApiKeySet -> {
                appData.saveAppId(intent.key)
                state.postValue(MainState.HeadlinesTab)
            }
        }
    }
}

sealed class MainIntent {
    object OnHeadlinesSelected : MainIntent()
    object OnSearchSelected : MainIntent()
    data class OnApiKeySet(val key: String) : MainIntent()
}

sealed class MainState {
    object PromptForId : MainState()
    object HeadlinesTab : MainState()
    object SearchTab : MainState()
}
