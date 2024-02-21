package com.diipl.moviebeam.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diipl.moviebeam.room.models.RentalMovieModel

@Dao
interface RentalMovieDao {

    @Insert(entity = RentalMovieModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(rentalMovieModel: RentalMovieModel)

    @Update
    suspend fun updateMovie(rentalMovieModel: RentalMovieModel)

    @Delete
    suspend fun deleteMovie(rentalMovieModel: RentalMovieModel)

    @Query("SELECT * FROM recentMovies GROUP BY releaseId ORDER BY lastTimeStamp DESC")
    fun getMovies(): LiveData<List<RentalMovieModel>>

    @Query("SELECT COUNT(*) FROM recentMovies WHERE releaseId = :releaseId")
    suspend fun getMovieCount(releaseId: Int): Int

    @Query("DELETE FROM recentMovies WHERE finishTimeStamp <= :timeStamp")
    suspend fun deleteMovieOverTime(timeStamp: Long)

    @Query("SELECT * FROM recentMovies WHERE releaseId = :releaseId")
    fun getRentalMovie(releaseId : Int): RentalMovieModel

    @Query("DELETE FROM recentMovies")
    suspend fun deleteAllMovies()

}