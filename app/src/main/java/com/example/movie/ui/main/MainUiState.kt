package com.example.movie.ui.main

import com.example.movie.model.Movie

data class MainUiState(
    val isLoading: Boolean,
    val movies: List<Movie>,
    val errorMessage: String?
)
