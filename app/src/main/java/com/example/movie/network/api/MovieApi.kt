package com.example.movie.network.api

import com.example.movie.model.MovieList
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("discover/movie")
    suspend fun getMoviesList(@Query("page") pageNumber: Int): MovieList

    @GET("search/movie")
    suspend fun getSearchList(@Query("query") query: String): MovieList
}