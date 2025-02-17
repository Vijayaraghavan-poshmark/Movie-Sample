package com.example.movie.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.model.Movie
import com.example.movie.network.repository.NetworkRepository
import com.example.movie.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    repository: NetworkRepository,
) : ViewModel() {
    val searchedMovie: StateFlow<SearchUiState>

    private val _loadingState = MutableStateFlow(false)
    private val _errorMsg = MutableStateFlow<String?>(null)
    private val _moviesState = MutableStateFlow<List<Movie>>(listOf())

    private val _searchText = MutableStateFlow("")

    private val _input = MutableSharedFlow<SearchInput>(extraBufferCapacity = 20)
    private val _output = MutableSharedFlow<SearchEvent>(extraBufferCapacity = 20)
    val output = _output.asSharedFlow()

    init {
        viewModelScope.launch {
            _searchText.filter { it.length > 2 }.debounce(300).collect {
                runCatching {
                    _loadingState.emit(true)
                    val moviesList = repository.getSearchList(it.trim())
                    _loadingState.emit(false)
                    _moviesState.value = moviesList.results
                    _errorMsg.value = null
                }.onFailure {
                    _loadingState.emit(false)
                    _errorMsg.emit(it.message)
                    _moviesState.emit(listOf())
                }
            }
        }

        searchedMovie =
            combine(_moviesState, _loadingState, _errorMsg) { movies, loading, errorMsg ->
                SearchUiState(isLoading = loading, movies, errorMsg)
            }.debounce(200).stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SearchUiState(false, listOf(), null)
            )

        viewModelScope.launch {
            _input.collect {
                when (it) {
                    SearchInput.BackClicked -> {
                        _output.tryEmit(SearchEvent.NavigateUp)
                    }
                    is SearchInput.SearchClicked -> {
                        _output.tryEmit(SearchEvent.NavigateTo(Screen.Detail(it.movie)))
                    }
                    is SearchInput.TextChanged -> {
                        _searchText.value = it.value
                    }
                }
            }
        }
    }

    fun sendEvent(event: SearchInput) {
        _input.tryEmit(event)
    }
}