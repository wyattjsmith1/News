package com.example.news.appdata

import android.content.Context

private const val APP_ID_KEY = "app_id"

class AndroidApplicationData(context: Context) : ApplicationData {

    private val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    override fun getAppId() = prefs.getString(APP_ID_KEY, null)

    override fun saveAppId(id: String) {
        prefs
            .edit()
            .putString(APP_ID_KEY, id)
            .apply()
    }
}
