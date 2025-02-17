package com.example.movie.ui.search

import com.example.movie.model.Movie
import com.example.movie.ui.navigation.Screen

data class SearchUiState(
    val isLoading: Boolean,
    val movies: List<Movie>,
    val errorMsg: String?
)

sealed interface SearchInput {
    data class TextChanged(val value: String): SearchInput
    data object BackClicked: SearchInput
    data class SearchClicked(val movie: Movie): SearchInput
}

sealed interface SearchEvent {
    data class NavigateTo(val screen: Screen): SearchEvent
    data object NavigateUp: SearchEvent
}
