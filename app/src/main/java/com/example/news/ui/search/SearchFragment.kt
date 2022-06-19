package com.example.news.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.data.Article
import com.example.news.ui.ArticleAdapter
import com.example.news.util.ErrorHandler
import com.example.news.util.UrlOpener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchFragment : Fragment(), KoinComponent {

    private val viewModel: SearchViewModel by viewModel()
    private val loadingIcon: ProgressBar by lazy {
        requireView().findViewById(R.id.loading_spinner)
    }

    private val articleAdapter = ArticleAdapter {
        viewModel.enqueueIntent(SearchIntent.ArticleTapped(it))
    }
    private val errorHandler by inject<ErrorHandler>()
    private val urlOpener by inject<UrlOpener>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
        }
        viewModel.state().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> displayLoading()
                is SearchState.DisplayArticles -> displayArticles(state.articles)
                is SearchState.Error -> context?.let { errorHandler.handleError(it, state.error) }
                is SearchState.OpenUrl -> context?.let { urlOpener.openUrl(it, state.url) }
            }
        }

        view.findViewById<EditText>(R.id.search).doOnTextChanged { text, _, _, _ ->
            viewModel.enqueueIntent(SearchIntent.SearchUpdated(text.toString()))
        }
    }

    private fun displayLoading() {
        loadingIcon.isVisible = true
    }

    private fun displayArticles(articles: List<Article>) {
        loadingIcon.isVisible = false
        articleAdapter.setArticles(articles)
    }
}
