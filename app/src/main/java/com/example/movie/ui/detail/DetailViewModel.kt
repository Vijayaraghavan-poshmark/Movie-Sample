package com.example.movie.ui.detail

import androidx.lifecycle.ViewModel
import com.example.movie.di.BaseImageUrl
import com.example.movie.di.BaseOriginalImageUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @BaseOriginalImageUrl val imageUrl: String
): ViewModel() {
}