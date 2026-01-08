package com.example.assignment1.Hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("products")
    fun provideProductRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("users")
    fun provideUserRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("products")
    fun productProvideApiService(@Named("products") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    @Named("users")
    fun userProvideApiService(@Named("users") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
