package com.diipl.moviebeam.di

import android.content.Context
import androidx.work.WorkManager
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.room.dao.ProgramGuideDao
import com.diipl.moviebeam.room.dao.RentalMovieDao
import com.diipl.moviebeam.room.db.MoviesDatabase
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {


    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MoviesDatabase {
        return MoviesDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideRentalMovieDao(database: MoviesDatabase): RentalMovieDao {
        return database.movieDao()
    }

    @Singleton
    @Provides
    fun providePreference(@ApplicationContext appContext: Context): SharedPreference {
        return SharedPreference(appContext)
    }

    @Singleton
    @Provides
    fun provideProgramGuideDao(database: MoviesDatabase): ProgramGuideDao {
        return database.programGuideDao()
    }

    @Singleton
    @Provides
    fun providePreferenceHandler(@ApplicationContext appContext: Context): PreferenceHandler {
        return PreferenceHandler(appContext)
    }

    @Singleton
    @Provides
    fun providePreferenceDataStoreHelper(@ApplicationContext appContext: Context): PreferenceDataStoreHelper {
        return PreferenceDataStoreHelper(appContext)
    }

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager {
        return WorkManager.getInstance(appContext)
    }

}