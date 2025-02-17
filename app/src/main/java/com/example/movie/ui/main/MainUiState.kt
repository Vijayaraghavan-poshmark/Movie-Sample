package com.example.movie.ui.main

import com.example.movie.model.Movie
import com.example.movie.ui.navigation.Screen

data class MainUiState(
    val isLoading: Boolean,
    val movies: List<Movie>,
    val errorMessage: String?
)

sealed interface MainInput {
    data class MovieClicked(val movie: Movie): MainInput
    object LoadMore: MainInput
}

sealed interface MainActionEvent {
    data class NavigateTo(val screen: Screen): MainActionEvent
}
