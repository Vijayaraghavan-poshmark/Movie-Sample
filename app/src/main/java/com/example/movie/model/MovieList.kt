package com.example.movie.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MovieList(
    @SerializedName("page") var page: Int? = null,
    @SerializedName("results") var results: ArrayList<Movie> = arrayListOf(),
    @SerializedName("total_pages") var total_pages: Int? = null,
    @SerializedName("total_results") var total_results: Int? = null
)