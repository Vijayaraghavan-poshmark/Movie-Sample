package com.example.movie.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiDispatcher
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.example.movie.network.repository.NetworkRepository
import com.example.movie.ui.StateHolder
import com.example.movie.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val presenter: SearchPresenter,
) : ViewModel() {

    private val scope = CoroutineScope(viewModelScope.coroutineContext + AndroidUiDispatcher.Main)
    val searchedMovie = scope.launchMolecule(RecompositionMode.ContextClock) {
        presenter.generateUi()
    }

    fun sendEvent(event: SearchInput) {
        presenter.sendEvent(event)
    }

}

class SearchPresenter @Inject constructor(
    private val repository: NetworkRepository,
) {
    private val state = StateHolder()
    private val _searchText = MutableStateFlow("")
    private val _input = MutableSharedFlow<SearchInput>(extraBufferCapacity = 20)

    @Composable
    fun generateUi(): SearchUiState {
        InitialCall()
        HandleEvent()
        return state.toSearchUi()
    }

    @Composable
    fun InitialCall() = LaunchedEffect(Unit) {
        _searchText.filter { it.length > 2 }.debounce(300).collect {
            runCatching {
                state.isLoading = true
                val moviesList = repository.getSearchList(it.trim())
                state.isLoading = false
                state.movies.clear()
                state.movies.addAll(moviesList.results)
                state.errorMessage = null
            }.onFailure {
                state.isLoading = false
                state.errorMessage = it.message
                state.movies.clear()
            }
        }
    }

    @Composable
    private fun HandleEvent() {
        LaunchedEffect(Unit) {
            _input.collect {
                when (it) {
                    SearchInput.BackClicked -> {
                        state.event = SearchEvent.NavigateUp
                    }
                    is SearchInput.SearchClicked -> {
                        state.event = SearchEvent.NavigateTo(Screen.Detail(it.movie))
                    }
                    is SearchInput.TextChanged -> {
                        _searchText.value = it.value
                    }

                    SearchInput.Clear -> state.event = null
                }
            }
        }
    }

    fun sendEvent(event: SearchInput) {
        _input.tryEmit(event)
    }
}
