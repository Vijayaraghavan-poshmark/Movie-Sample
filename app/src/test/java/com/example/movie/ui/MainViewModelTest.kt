package com.example.movie.ui

import app.cash.turbine.test
import com.example.movie.model.Movie
import com.example.movie.network.repository.FakeNetworkRepositoryImpl
import com.example.movie.ui.main.MainInput
import com.example.movie.ui.main.MainUiState
import com.example.movie.ui.main.MainViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel(
            FakeNetworkRepositoryImpl(),
            "test"
        )
    }

    @Test
    fun `test single movie list`() = runTest {
        viewModel.uiState.test {
            var response = awaitItem()
            assert(MainUiState(isLoading=true, movies= listOf(), errorMessage=null) == response)
            response = awaitItem()
            val movieList = MainUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMessage=null)
            assert(movieList == response)
        }
    }

    @Test
    fun `test twice movie list`() = runTest {
        viewModel.uiState.test {
            var response = awaitItem()
            assert(MainUiState(isLoading=true, movies= listOf(), errorMessage=null) == response)
            response = awaitItem()
            var movieList = MainUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMessage=null)
            assert(movieList == response)
            viewModel.setInputAction(MainInput.LoadMore)
            response = awaitItem()
            movieList = MainUiState(isLoading=true, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null)), errorMessage=null)
            assert(movieList == response)

            response = awaitItem()
            movieList = MainUiState(isLoading=false, movies= listOf(Movie(adult=false, backdropPath=null, id=1241982, originalLanguage=null, originalTitle=null, overview="abc description", popularity=4401.583, posterPath=null, releaseDate=null, title="abc", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=939243, originalLanguage=null, originalTitle=null, overview="def description", popularity=3435.264, posterPath=null, releaseDate=null, title="def", video=false, voteAverage=null, voteCount=null),  Movie(adult=false, backdropPath=null, id=933260, originalLanguage=null, originalTitle=null, overview="efg", popularity=680.179, posterPath=null, releaseDate=null, title="efg", video=false, voteAverage=null, voteCount=null), Movie(adult=false, backdropPath=null, id=1357633, originalLanguage=null, originalTitle=null, overview="ghi descpriton", popularity=568.761, posterPath=null, releaseDate=null, title="ghi", video=false, voteAverage=null, voteCount=null)), errorMessage=null)
            assert(movieList == response)
        }
    }

    @Test
    fun `test error response`() = runTest {
        viewModel.uiState.test {
            skipItems(2)
            viewModel.setInputAction(MainInput.LoadMore)
            skipItems(2)
            viewModel.setInputAction(MainInput.LoadMore)
            skipItems(1)
            val response = awaitItem()
            assert(response == MainUiState(isLoading=false, movies= listOf(), errorMessage="Error"))
        }

    }
}