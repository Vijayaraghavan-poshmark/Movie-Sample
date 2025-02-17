package com.example.movie.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.movie.model.Movie
import com.example.movie.ui.detail.DetailScreen
import com.example.movie.ui.detail.DetailViewModel
import com.example.movie.ui.main.MainScreen
import com.example.movie.ui.main.MainViewModel
import com.example.movie.ui.search.SearchScreen
import com.example.movie.ui.search.SearchViewModel
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
sealed class Screen(val title: String) {
    @Serializable
    data object Main : Screen("Main")

    @Serializable
    data class Detail(val movie: Movie) : Screen("Detail")

    @Serializable
    data object Search : Screen("Search")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAppBar(
    title: String,
    canNavigateUp: Boolean,
    onNavigateUp: () -> Unit,
    canShowSearchIcon: Boolean,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        },
        actions = {
            if (canShowSearchIcon) {
                IconButton(onClick = onSearchClicked) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
        }
    )
}

@Composable
fun MovieApp(
    navController: NavHostController = rememberNavController()
) {
    var title by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (title != Screen.Search.title) {
                MovieAppBar(
                    title = title,
                    canNavigateUp = navController.previousBackStackEntry != null,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    canShowSearchIcon = title == Screen.Main.title,
                    onSearchClicked = {
                        navController.navigate(Screen.Search)
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Main,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<Screen.Main> {
                title = it.toRoute<Screen.Main>().title
                val viewModel = hiltViewModel<MainViewModel>()
                val uiState by viewModel.uiState.collectAsState()
                val listState = rememberLazyGridState()
                MainScreen(
                    listState = listState,
                    movies = uiState.movies,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    baseImageUrl = viewModel.baseImageUrl,
                    loadMore = viewModel::loadMore,
                    onClick = {
                        navController.navigate(Screen.Detail(it))
                    }
                )
            }

            composable<Screen.Detail>(
                typeMap = mapOf(
                    typeOf<Movie>() to DetailNavType.detailType
                )
            ) {
                val viewModel = hiltViewModel<DetailViewModel>()
                val argument = it.toRoute<Screen.Detail>()
                title = argument.title
                DetailScreen(
                    movie = argument.movie,
                    baseImageUrl = viewModel.imageUrl
                )
            }

            composable<Screen.Search> {
                title = it.toRoute<Screen.Search>().title
                val viewModel = hiltViewModel<SearchViewModel>()
                val queriedMovies by viewModel.searchedMovie.collectAsState()

                SearchScreen(
                    onTextChanged = viewModel::onTextChanged,
                    onMovieClicked = {
                        navController.navigate(Screen.Detail(it))
                    },
                    queriedMovies = queriedMovies.movies,
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    isLoading = queriedMovies.isLoading,
                    errorMessage = queriedMovies.errorMsg,
                )
            }
        }
    }
}
