package com.example.news.util

import android.content.Context
import android.widget.Toast
import com.example.news.data.NewsApiError

class ErrorHandler {
    fun handleError(context: Context, error: NewsApiError) {
        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
    }
}