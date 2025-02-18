package com.example.movie.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.example.movie.di.BaseImageUrl
import com.example.movie.model.Movie
import com.example.movie.network.repository.NetworkRepository
import com.example.movie.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val presenter: MainPresenter,
    @BaseImageUrl val baseImageUrl: String,
) : ViewModel() {

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)
    val uiState = scope.launchMolecule(RecompositionMode.ContextClock) {
        presenter.generateUi()
    }

    fun setInputAction(input: MainInput) {
        presenter.setInputAction(input)
    }
}

class MainPresenter @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    private val state = StateHolder()
    private var currentPage = 1
    private val mutex = Mutex()
    private var isRunning = false

    private val _inputs = MutableSharedFlow<MainInput>(extraBufferCapacity = 10)

    @Composable
    fun generateUi(): MainUiState {
        HandleEvent()
        LaunchedEffect(Unit) {
            getMovieList(state)
        }
        return state.toMainUi()
    }

    @Composable
    fun HandleEvent() = LaunchedEffect(Unit) {
        _inputs.collect {
            when (it) {
                MainInput.LoadMore -> loadMore(state)

                is MainInput.MovieClicked -> {
                    state.event = MainActionEvent.NavigateTo(Screen.Detail(it.movie))
                }

                MainInput.Clear -> {
                    state.event = null
                }
            }
        }
    }

    private suspend fun loadMore(state: StateHolder) {
        if (!isRunning) {
            getMovieList(state)
        }
    }

    private suspend fun getMovieList(state: StateHolder) {
        mutex.withLock {
            isRunning = true
            runCatching {
                state.isLoading = true
                val movieList = networkRepository.getMoviesList(currentPage)
                if (currentPage + 1 <= (movieList.total_pages ?: 0)) {
                    currentPage++
                }
                state.isLoading = false
                state.movies.addAll(movieList.results)
                state.errorMessage = null
            }.onFailure {
                state.isLoading = false
                state.errorMessage = "Error"
                state.movies.clear()
            }
            isRunning = false
        }
    }

    fun setInputAction(input: MainInput) {
        _inputs.tryEmit(input)
    }
}

@Stable
internal class StateHolder {
    var isLoading by mutableStateOf(true)
    val movies = mutableStateListOf<Movie>()
    var errorMessage by mutableStateOf<String?>(null)
    var event by mutableStateOf<MainActionEvent?>(null)

    fun toMainUi(): MainUiState {
        return MainUiState(
            isLoading,
            movies.toList(),
            errorMessage,
            event
        )
    }
}

