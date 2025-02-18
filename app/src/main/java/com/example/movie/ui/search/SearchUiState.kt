package com.example.movie.ui.search

import androidx.compose.runtime.Immutable
import com.example.movie.model.Movie
import com.example.movie.ui.navigation.Screen

@Immutable
data class SearchUiState(
    val isLoading: Boolean,
    val movies: List<Movie>,
    val errorMsg: String?,
    val event: SearchEvent?,
)

sealed interface SearchInput {
    data class TextChanged(val value: String): SearchInput
    data object BackClicked: SearchInput
    data class SearchClicked(val movie: Movie): SearchInput
    data object Clear: SearchInput
}

sealed interface SearchEvent {
    data class NavigateTo(val screen: Screen): SearchEvent
    data object NavigateUp: SearchEvent
}
