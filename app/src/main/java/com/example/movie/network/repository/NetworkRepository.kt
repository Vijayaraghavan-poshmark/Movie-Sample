package com.example.movie.network.repository

import com.example.movie.model.MovieList

interface NetworkRepository {
    suspend fun getMoviesList(pageNumber: Int): MovieList

    suspend fun getSearchList(query: String): MovieList
}