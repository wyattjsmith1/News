package com.example.news.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.news.AppIdDialogFragment
import com.example.news.R
import com.example.news.ui.headlines.HeadlinesFragment
import com.example.news.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val headlinesFragment = HeadlinesFragment()
    private val searchFragment = SearchFragment()
    private var hasAddedFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Process state changes
        viewModel.state().observe(this) {
            when (it) {
                is MainState.PromptForId -> promptForId()
                is MainState.HeadlinesTab -> setCurrentFragment(headlinesFragment)
                is MainState.SearchTab -> setCurrentFragment(searchFragment)
            }
        }

        // Update ViewModel when tabs are pressed
        findViewById<BottomNavigationView>(R.id.navigation_bar).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.headlines -> viewModel.enqueueIntent(MainIntent.OnHeadlinesSelected)
                R.id.search -> viewModel.enqueueIntent(MainIntent.OnSearchSelected)
            }
            true
        }

        supportFragmentManager.setFragmentResultListener(
            AppIdDialogFragment.REQUEST_KEY,
            this
        ) { _, result ->
            viewModel.enqueueIntent(
                MainIntent.OnApiKeySet(
                    requireNotNull(
                        result.getString(
                            AppIdDialogFragment.APP_ID
                        )
                    )
                )
            )
        }
    }

    private fun promptForId() {
        AppIdDialogFragment().show(supportFragmentManager, "app_id_prompt")
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_holder, fragment)
            if (hasAddedFragment) {
                addToBackStack(null)
            }
            hasAddedFragment = true
            commit()
        }
    }
}
