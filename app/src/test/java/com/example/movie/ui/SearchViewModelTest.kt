package com.example.movie.ui

import app.cash.turbine.test
import com.example.movie.model.Movie
import com.example.movie.network.repository.FakeNetworkRepositoryImpl
import com.example.movie.ui.search.SearchInput
import com.example.movie.ui.search.SearchUiState
import com.example.movie.ui.search.SearchViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(
            FakeNetworkRepositoryImpl()
        )
    }

    @Test
    fun `test search`() = runTest {
        viewModel.searchedMovie.test {
            var response = awaitItem()
            assert(response == SearchUiState(isLoading=false, movies= listOf(), errorMsg=null))
            viewModel.sendEvent(SearchInput.TextChanged("abc"))
            response = awaitItem()
            assert(response == SearchUiState(isLoading=true, movies= listOf(), errorMsg=null))
            response = awaitItem()
            assert(response == SearchUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=974453, originalLanguage=null, originalTitle=null, overview="abc", popularity=405.938, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=446681, originalLanguage=null, originalTitle=null, overview="def", popularity=0.564, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMsg=null))
        }
    }

    @Test
    fun `test error response`() = runTest {
        viewModel.searchedMovie.test {
            var response = awaitItem()
            assert(response == SearchUiState(isLoading=false, movies= listOf(), errorMsg=null))
            viewModel.sendEvent(SearchInput.TextChanged("error"))
            response = awaitItem()
            assert(response == SearchUiState(isLoading=true, movies= listOf(), errorMsg=null))
            response = awaitItem()
            assert(response == SearchUiState(isLoading=false, movies= listOf(), errorMsg="No Result"))
        }
    }

    @Test
    fun `test dont send if search query is less than 2 length`() = runTest {
        viewModel.searchedMovie.test {
            var response = awaitItem()
            assert(response == SearchUiState(isLoading = false, movies = listOf(), errorMsg = null))
            viewModel.sendEvent(SearchInput.TextChanged("a"))
            expectNoEvents()
            viewModel.sendEvent(SearchInput.TextChanged("ab"))
            expectNoEvents()
        }
    }
}