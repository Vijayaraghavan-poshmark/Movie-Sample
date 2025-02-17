package com.example.movie.network.repository

import com.example.movie.MockResponseFileReader
import com.example.movie.model.MovieList
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

class FakeNetworkRepositoryImpl: NetworkRepository {

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    override suspend fun getMoviesList(pageNumber: Int): MovieList {
        delay(500)
        return when (pageNumber) {
            1 -> json.decodeFromString<MovieList>(MockResponseFileReader("move_list_1.json").content)
            2 -> json.decodeFromString<MovieList>(MockResponseFileReader("move_list_2.json").content)
            else -> throw IllegalArgumentException("Unable to perform this action")
        }
    }

    override suspend fun getSearchList(query: String): MovieList {
        delay(500)
        return when(query) {
            "abc" -> json.decodeFromString<MovieList>(MockResponseFileReader("search_1.json").content)
            else -> throw IllegalArgumentException("No Result")
        }
    }
}