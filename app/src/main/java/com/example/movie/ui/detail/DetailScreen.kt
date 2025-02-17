package com.example.movie.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.movie.model.Movie

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(
    movie: Movie,
    baseImageUrl: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Column {
            GlideImage(
                model = baseImageUrl + movie.backdropPath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Text(
                text = movie.title ?: "No Title"
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Text(
                text = movie.overview.orEmpty()
            )
        }
    }
}