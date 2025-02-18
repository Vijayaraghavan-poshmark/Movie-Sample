package com.example.movie.ui

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.example.movie.model.Movie
import com.example.movie.network.repository.FakeNetworkRepositoryImpl
import com.example.movie.ui.main.MainInput
import com.example.movie.ui.main.MainPresenter
import com.example.movie.ui.main.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var presenter: MainPresenter

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        presenter = MainPresenter(
            FakeNetworkRepositoryImpl(),
        )
    }

    @Test
    fun `test single movie list`() = runTest {
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.generateUi()
        }.test {
            var response = awaitItem()
            assert(MainUiState(isLoading=true, movies= listOf(), errorMessage=null, event = null) == response)
            skipItems(1)
            response = awaitItem()
            val movieList = MainUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMessage=null, event = null)
            assert(movieList == response)
        }
    }

    @Test
    fun `test twice movie list`() = runTest {
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.generateUi()
        }.test {
            var response = awaitItem()
            assert(MainUiState(isLoading=true, movies= listOf(), errorMessage=null, event = null) == response)
            skipItems(1)
            response = awaitItem()
            var movieList = MainUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMessage=null, event = null)
            assert(movieList == response)
            presenter.setInputAction(MainInput.LoadMore)
            response = awaitItem()
            movieList = MainUiState(isLoading=true, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMessage=null, event = null)
            assert(movieList == response)

            skipItems(1)
            response = awaitItem()
            movieList = MainUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null),  Movie(adult=false, backdropPath=null, id=933260, originalLanguage=null, originalTitle=null, overview="efg", popularity=680.179, posterPath=null, releaseDate=null, title="efg", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=1357633, originalLanguage=null, originalTitle=null, overview="ghi descpriton", popularity=568.761, posterPath=null, releaseDate=null, title="ghi", video=false, voteAverage=null, voteCount=null)), errorMessage=null, event = null)
            assert(movieList == response)
        }
    }

    @Test
    fun `test error response`() = runTest {
        moleculeFlow(RecompositionMode.Immediate) {
            presenter.generateUi()
        }.test {
            skipItems(3)
            presenter.setInputAction(MainInput.LoadMore)
            skipItems(3)
            presenter.setInputAction(MainInput.LoadMore)
            skipItems(1)
            var response = awaitItem()
            assert(!response.isLoading)
            response = awaitItem()
            assert("Error" == response.errorMessage)
            response = awaitItem()
            assert(response.movies.isEmpty())
        }

    }
}
