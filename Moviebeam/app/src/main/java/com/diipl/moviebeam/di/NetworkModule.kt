package com.diipl.moviebeam.di

import androidx.databinding.ktx.BuildConfig
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.remote.services.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient() : OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(Constants.API_TIME_OUT_IN_SEC,TimeUnit.SECONDS)
            .connectTimeout(Constants.API_TIME_OUT_IN_SEC,TimeUnit.SECONDS)
            .build()
    } else
        OkHttpClient
            .Builder()
            .readTimeout(Constants.API_TIME_OUT_IN_SEC,TimeUnit.SECONDS)
            .connectTimeout(Constants.API_TIME_OUT_IN_SEC,TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


}