package com.example.news

import android.app.Application
import com.example.news.appdata.AndroidApplicationData
import com.example.news.appdata.ApplicationData
import com.example.news.data.NewsApiError
import com.example.news.data.NewsApiRepository
import com.example.news.ui.headlines.HeadlinesViewModel
import com.example.news.ui.main.MainViewModel
import com.example.news.ui.search.SearchViewModel
import com.example.news.util.ErrorHandler
import com.example.news.util.UrlOpener
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule)
        }
    }
}

private val appModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().apply {

                    // Handle errors
                    addInterceptor {
                        val response = it.proceed(it.request())
                        val body = response.body()
                        if (!response.isSuccessful && body != null) {
                            throw Gson().fromJson(body.charStream(), NewsApiError::class.java)
                        }
                        response
                    }
                }.build()
            )
            .build()
    }

    single<NewsApiRepository> {
        get<Retrofit>().create(NewsApiRepository::class.java)
    }

    single<ApplicationData> {
        AndroidApplicationData(androidContext())
    }

    single { UrlOpener() }
    single { ErrorHandler() }

    viewModel { MainViewModel(get()) }
    viewModel { HeadlinesViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
}
