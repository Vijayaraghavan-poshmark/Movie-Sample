package com.example.movie.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.movie.model.Movie
import com.example.movie.model.UiState

@Composable
fun MainScreen(
    listState: LazyGridState,
    movies: List<Movie>,
    isLoading: Boolean,
    errorMessage: String?,
    baseImageUrl: String,
    onClick: (Movie) -> Unit,
    loadMore: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // observe list scrolling
        val reachedBottom: Boolean by remember {
            derivedStateOf {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
            }
        }

        // load more if scrolled to bottom
        LaunchedEffect(reachedBottom) {
            if (reachedBottom) loadMore()
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(
                text = "Error loading...$errorMessage",
            )
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(8.dp)
            ) {
                items(movies) {
                    SingleMovie(
                        imageUrl = baseImageUrl + it.posterPath,
                        title = it.title.orEmpty(),
                        modifier = Modifier
                            .clickable {
                                onClick(it)
                            }
                    )
                }

                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SingleMovie(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (titleRef, imageRef) = createRefs()

        GlideImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(imageRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }

        )

        Text(
            text = title,
            color = Color.White,
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Black,
                            Color.LightGray
                        )
                    ),
                    alpha = 0.6f
                )
                .constrainAs(titleRef) {
                    bottom.linkTo(imageRef.bottom)
                    start.linkTo(imageRef.start)
                    end.linkTo(imageRef.end)
                },
            maxLines = 1,
        )
    }
}

