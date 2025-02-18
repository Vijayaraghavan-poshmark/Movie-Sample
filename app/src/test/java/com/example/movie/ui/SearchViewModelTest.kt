package com.example.movie.ui

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.example.movie.model.Movie
import com.example.movie.network.repository.FakeNetworkRepositoryImpl
import com.example.movie.ui.search.SearchInput
import com.example.movie.ui.search.SearchPresenter
import com.example.movie.ui.search.SearchUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {

    private lateinit var presenter: SearchPresenter

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        presenter = SearchPresenter(
            FakeNetworkRepositoryImpl()
        )
    }

    @Test
    fun `test search`() = runTest {
        moleculeFlow(RecompositionMode.Immediate) { presenter.generateUi() }.test {
            var response = awaitItem()
            assert(!response.isLoading)
            assert(response.movies.isEmpty())
            assert(response.errorMsg == null)
            presenter.sendEvent(SearchInput.TextChanged("abc"))
            response = awaitItem()
            assert(response.isLoading)
            assert(response.movies.isEmpty())
            assert(response.errorMsg == null)
            skipItems(2)
            response = awaitItem()
            assert(response == SearchUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=974453, originalLanguage=null, originalTitle=null, overview="abc", popularity=405.938, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=446681, originalLanguage=null, originalTitle=null, overview="def", popularity=0.564, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMsg=null, event = null))
        }
    }

    @Test
    fun `test error response`() = runTest {
        moleculeFlow(RecompositionMode.Immediate) { presenter.generateUi() }.test {
            var response = awaitItem()
            assert(!response.isLoading)
            assert(response.movies.isEmpty())
            assert(response.errorMsg == null)
            presenter.sendEvent(SearchInput.TextChanged("error"))
            response = awaitItem()
            assert(response.isLoading)
            skipItems(1)
            response = awaitItem()
            assert(response.errorMsg == "No Result")
            response = awaitItem()
            assert(response.movies.isEmpty())

        }
    }

    @Test
    fun `test dont send if search query is less than 2 length`() = runTest {
        moleculeFlow(RecompositionMode.Immediate) { presenter.generateUi() }.test {
            var response = awaitItem()
            assert(!response.isLoading)
            assert(response.movies.isEmpty())
            assert(response.errorMsg == null)
            presenter.sendEvent(SearchInput.TextChanged("a"))
            expectNoEvents()
            presenter.sendEvent(SearchInput.TextChanged("ab"))
            expectNoEvents()
        }
    }
}