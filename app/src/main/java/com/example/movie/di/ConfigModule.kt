package com.example.movie.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ConfigModule {

    @BaseUrl
    @Singleton
    @Provides
    fun provideBaseUrl(): String = "https://api.themoviedb.org/3/"

    @BaseImageUrl
    @Singleton
    @Provides
    fun provideBaseImageUrl(): String = "https://image.tmdb.org/t/p/w500"

    @BaseOriginalImageUrl
    @Singleton
    @Provides
    fun provideBaseImageOriginalUrl(): String = "https://image.tmdb.org/t/p/original"

    @AuthToken
    @Singleton
    @Provides
    fun provideAuthToken(): String = "change me"

}