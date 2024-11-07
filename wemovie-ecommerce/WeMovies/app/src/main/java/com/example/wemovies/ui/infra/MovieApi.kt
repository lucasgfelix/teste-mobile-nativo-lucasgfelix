package com.example.wemovies.ui.infra


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieApi {
    private const val BASE_URL = "https://wefit-movies.vercel.app/api/"

    val retrofitService: MovieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApiService::class.java)
    }
}