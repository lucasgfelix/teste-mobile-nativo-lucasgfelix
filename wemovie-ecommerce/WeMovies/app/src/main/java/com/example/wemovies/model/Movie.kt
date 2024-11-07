package com.example.wemovies.model

data class Movie(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    var quantity: Int
)

