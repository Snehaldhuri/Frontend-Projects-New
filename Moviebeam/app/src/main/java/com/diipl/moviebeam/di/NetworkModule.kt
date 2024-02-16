package com.diipl.moviebeam.di

import androidx.databinding.ktx.BuildConfig
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.remote.services.AccountSetupApiService
import com.diipl.moviebeam.data.remote.services.AssetApiService
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.remote.services.MoviesAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
            .connectTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
            .build()
    } else
        OkHttpClient
            .Builder()
            .readTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
            .connectTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    @Named(Constants.LG_REST)
    fun provideRetrofitLGREST(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Constants.BASE_URL_LG_REST)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @Named(Constants.ACCOUNT_SETUP)
    fun provideRetrofitAccountSetup(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Constants.BASE_URL_ACCOUNT_SETUP)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @Named(Constants.ASSET)
    fun provideRetrofitAsset(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Constants.BASE_URL_ASSET)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @Named(Constants.MOVIE_ACCESS)
    fun provideRetrofitMovie(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Constants.BASE_URL_MOVIE_RENTAL)
        .client(okHttpClient)
        .build()

    @Provides
    fun provideLgRestApiService(@Named(Constants.LG_REST) retrofit: Retrofit): LgRestApiService =
        retrofit.create(LgRestApiService::class.java)

    @Provides
    fun provideACCOUNTSETUPApiService(@Named(Constants.ACCOUNT_SETUP) retrofit: Retrofit): AccountSetupApiService =
        retrofit.create(AccountSetupApiService::class.java)

    @Provides
    fun provideAssetApiService(@Named(Constants.ASSET) retrofit: Retrofit): AssetApiService =
        retrofit.create(AssetApiService::class.java)

    @Provides
    fun provideMovieAPIService(@Named(Constants.MOVIE_ACCESS) retrofit: Retrofit) : MoviesAPIService =
        retrofit.create(MoviesAPIService::class.java)

}