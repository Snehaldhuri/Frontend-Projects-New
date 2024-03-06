package com.diipl.moviebeam.di

import android.content.Context
import com.diipl.moviebeam.room.dao.RentalMovieDao
import com.diipl.moviebeam.room.db.MoviesDatabase
import com.diipl.moviebeam.utils.SharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

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
 /*   @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager {
        return WorkManager.getInstance(appContext)
    }
*/
}