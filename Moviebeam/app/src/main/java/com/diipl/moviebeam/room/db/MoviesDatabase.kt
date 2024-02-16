package com.diipl.moviebeam.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diipl.moviebeam.room.dao.RentalMovieDao
import com.diipl.moviebeam.room.models.RentalMovieModel

@Database(entities = [RentalMovieModel::class], version = 1, exportSchema = false)
abstract class MoviesDatabase : RoomDatabase(){

    abstract fun movieDao() : RentalMovieDao

    companion object{

        @Volatile
        private var instance : MoviesDatabase? = null

        fun getDatabase(context: Context): MoviesDatabase {
            return instance ?: synchronized(this){
                Room.databaseBuilder(context, MoviesDatabase::class.java, "MovieBeam")
//                    .addMigrations(MIGRATION)
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }

        object MIGRATION : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }

        }

    }

}