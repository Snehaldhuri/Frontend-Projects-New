package com.diipl.moviebeam.di

import com.diipl.moviebeam.data.remote.services.AccountSetupApiService
import com.diipl.moviebeam.data.remote.services.AssetApiService
import com.diipl.moviebeam.data.remote.services.EpgApiService
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.remote.services.MoviesAPIService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.JsonOrStringConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient  {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
            .connectTimeout(Constants.API_TIME_OUT_IN_SEC, TimeUnit.SECONDS)
            .build()
    }

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

    @Singleton
    @Provides
    @Named(Constants.EPG)
    fun provideRetrofitEpg(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL_LG_REST)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @Named(Constants.SYS_INFO)
    fun provideRetrofitSysInfo(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(JsonOrStringConverterFactory())
        .baseUrl(Constants.BASE_URL_ASSET)
        .client(okHttpClient)
        .build()

    @Provides
    fun provideLgRestApiService(@Named(Constants.LG_REST) retrofit: Retrofit): LgRestApiService =
        retrofit.create(LgRestApiService::class.java)

    @Provides
    fun provideACCOUNTSETUPApiService(@Named(Constants.ACCOUNT_SETUP) retrofit: Retrofit): AccountSetupApiService =
        retrofit.create(AccountSetupApiService::class.java)

    @Provides
    @Named(Constants.ASSET)
    fun provideAssetApiService(@Named(Constants.ASSET) retrofit: Retrofit): AssetApiService =
        retrofit.create(AssetApiService::class.java)

    @Provides
    fun provideMovieAPIService(@Named(Constants.MOVIE_ACCESS) retrofit: Retrofit): MoviesAPIService =
        retrofit.create(MoviesAPIService::class.java)

    @Provides
    fun provideEpgAPIService(@Named(Constants.EPG) retrofit: Retrofit): EpgApiService =
        retrofit.create(EpgApiService::class.java)

    @Provides
    @Named(Constants.SYS_INFO)
    fun provideSysInfoService(@Named(Constants.SYS_INFO) retrofit: Retrofit): AssetApiService =
        retrofit.create(AssetApiService::class.java)

}