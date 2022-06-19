package com.example.news.appdata

interface ApplicationData {
    fun getAppId(): String?
    fun saveAppId(id: String)
}
