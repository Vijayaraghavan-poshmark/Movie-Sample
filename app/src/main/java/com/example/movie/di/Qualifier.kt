package com.example.movie.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseImageUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOriginalImageUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthToken
