package com.example.movie.di

import com.example.movie.network.api.MovieApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @BaseUrl baseUrl: String
    ): Retrofit = Retrofit
        .Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideMovieService(
        retrofit: Retrofit
    ): MovieApi = retrofit.create(MovieApi::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @AuthToken authToken: String,
    ): OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .build()
        chain.proceed(newRequest)
    }.build()
}