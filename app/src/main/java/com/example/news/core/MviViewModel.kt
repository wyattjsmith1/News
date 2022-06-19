package com.example.news.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class MviViewModel<Intent, State>(initialState: State) : ViewModel() {
    private val intents: Channel<Intent> = Channel(Channel.RENDEZVOUS)

    protected val state by lazy {
        MutableLiveData<State>(initialState)
    }

    init {
        intents
            .consumeAsFlow()
            .onEach {
                processIntent(it)
            }
            .launchIn(viewModelScope)
    }

    abstract fun processIntent(intent: Intent)

    fun state(): LiveData<State> = state

    fun enqueueIntent(intent: Intent) {
        viewModelScope.launch { intents.send(intent) }
    }
}
