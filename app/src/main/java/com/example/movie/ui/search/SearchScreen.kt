package com.example.movie.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.movie.di.BaseImageUrl
import com.example.movie.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTextChanged: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onMovieClicked: (Movie) -> Unit,
    queriedMovies: List<Movie>,
    isLoading: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier,
    ) {
        val (topBarRef, searchResultRef, loadingRef, errorMsgRef) = createRefs()
        MovieSearchToolBar(
            onTextChanged = onTextChanged,
            onNavigateUp = onNavigateUp,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(TopAppBarDefaults.windowInsets)
                .clipToBounds()
                .heightIn(max = TopAppBarDefaults.TopAppBarExpandedHeight)
                .constrainAs(topBarRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(searchResultRef.top)
                }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .constrainAs(loadingRef) {
                        start.linkTo(parent.start)
                        top.linkTo(topBarRef.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )
        } else if (errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .constrainAs(errorMsgRef) {
                        start.linkTo(parent.start)
                        top.linkTo(topBarRef.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .constrainAs(searchResultRef) {
                        start.linkTo(parent.start)
                        top.linkTo(topBarRef.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                items(queriedMovies) {
                    SingleSearchMovie(
                        movie = it,
                        baseImageUrl = "",
                        modifier = Modifier
                            .clickable {
                                onMovieClicked(it)
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SingleSearchMovie(
    movie: Movie,
    baseImageUrl: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        GlideImage(
            model = baseImageUrl + movie.backdropPath,
            contentDescription = null,
        )

        Spacer(
            modifier = Modifier
                .width(8.dp)
        )

        Text(
            text = movie.title ?: "No title"
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchToolBar(
    onTextChanged: (String) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var search by remember { mutableStateOf("") }
    Row(
        modifier = modifier
    ) {

        IconButton(onClick = {
            onNavigateUp()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back"
            )
        }

        TextField(
            value = search,
            onValueChange = {
                search = it
                onTextChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(disabledIndicatorColor = Color.Transparent),
            textStyle = LocalTextStyle.current.copy(textDecoration = TextDecoration.None),
        )
    }
}