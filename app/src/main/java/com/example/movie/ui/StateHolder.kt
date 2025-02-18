package com.example.movie.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.movie.model.Movie
import com.example.movie.ui.search.SearchEvent
import com.example.movie.ui.search.SearchUiState

@Stable
internal class StateHolder {
    var isLoading by mutableStateOf(false)
    var movies = mutableStateListOf<Movie>()
    var errorMessage by mutableStateOf<String?>(null)
    var event by mutableStateOf<SearchEvent?>(null)

    fun toSearchUi(): SearchUiState = SearchUiState(
        isLoading,
        movies.toList(),
        errorMessage,
        event
    )
}
