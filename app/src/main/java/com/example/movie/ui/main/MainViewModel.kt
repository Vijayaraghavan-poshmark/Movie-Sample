package com.example.movie.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie.di.BaseImageUrl
import com.example.movie.model.Movie
import com.example.movie.network.repository.NetworkRepository
import com.example.movie.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    @BaseImageUrl val baseImageUrl: String,
) : ViewModel() {

    var uiState: StateFlow<MainUiState>

    private val _moviesState = MutableStateFlow<List<Movie>>(listOf())
    private val _loadingState = MutableStateFlow(true)
    private val _errorMsg = MutableStateFlow<String?>(null)

    private var currentPage = 1
    private val mutex = Mutex()
    private var isRunning = false

    private val _inputs = MutableSharedFlow<MainInput>(extraBufferCapacity = 10)
    private val _outputs = MutableSharedFlow<MainActionEvent>(extraBufferCapacity = 10)
    val output = _outputs.asSharedFlow()

    init {
        uiState = combine(_moviesState, _loadingState, _errorMsg) { movies, loading, errorMsg ->
            MainUiState(movies = movies, isLoading = loading, errorMessage = errorMsg)
        }.onStart { getMovieList() }
        .debounce(200)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            MainUiState(true, listOf(), null)
        )

        viewModelScope.launch {
            _inputs.collect {
                when (it) {
                    MainInput.LoadMore -> loadMore()
                    is MainInput.MovieClicked -> {
                        _outputs.tryEmit(MainActionEvent.NavigateTo(Screen.Detail(it.movie)))
                    }
                }
            }
        }
    }

    fun setInputAction(input: MainInput) {
        _inputs.tryEmit(input)
    }

    private fun loadMore() {
        if (!isRunning) {
            getMovieList()
        }
    }

    private fun getMovieList() {
        viewModelScope.launch {
            mutex.withLock {
                isRunning = true
                runCatching {
                    _loadingState.emit(true)
                    val movieList = networkRepository.getMoviesList(currentPage)
                    if (currentPage + 1 <= (movieList.total_pages ?: 0)) {
                        currentPage++
                    }
                    _loadingState.emit(false)
                    _moviesState.value += movieList.results
                    _errorMsg.emit(null)
                }.onFailure {
                    _loadingState.emit(false)
                    _errorMsg.emit("Error")
                    _moviesState.emit(listOf())
                }
                isRunning = false
            }

        }
    }
}
