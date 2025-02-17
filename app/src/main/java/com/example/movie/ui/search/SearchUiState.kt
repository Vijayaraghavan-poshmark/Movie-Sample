package com.example.movie.ui.search

import com.example.movie.model.Movie

data class SearchUiState(
    val isLoading: Boolean,
    val movies: List<Movie>,
    val errorMsg: String?
)