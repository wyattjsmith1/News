package com.example.news.util

import android.content.Context
import android.content.Intent
import android.net.Uri


class UrlOpener {
    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    }
}