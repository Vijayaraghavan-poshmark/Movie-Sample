package com.example.movie.network.repository

import com.example.movie.model.MovieList
import com.example.movie.network.api.MovieApi
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi
): NetworkRepository {
    override suspend fun getMoviesList(pageNumber: Int): MovieList {
        return movieApi.getMoviesList(pageNumber)
    }

    override suspend fun getSearchList(query: String): MovieList {
        return movieApi.getSearchList(query)
    }
}