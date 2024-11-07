package com.example.wemovies.ui.infra

import com.example.wemovies.model.MovieResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MovieApiService {

    @GET("api/movies")
    suspend fun getMovies(): Response<MovieResponse>

    companion object {
        private const val BASE_URL = "https://wefit-movies.vercel.app/"

        fun create(): MovieApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(MovieApiService::class.java)
        }
    }
}