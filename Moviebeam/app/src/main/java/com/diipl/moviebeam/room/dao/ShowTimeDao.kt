package com.diipl.moviebeam.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diipl.moviebeam.room.models.ShowTimeModel

@Dao
interface ShowTimeDao {

    @Insert(entity = ShowTimeModel::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShow(showTimeModel: ShowTimeModel)

    @Update
    suspend fun updateShow(showTimeModel: ShowTimeModel)

    @Delete
    suspend fun deleteShow(showTimeModel: ShowTimeModel)

    @Query("SELECT * FROM recentShows GROUP BY releaseId ORDER BY lastTimeStamp DESC")
    fun getShows(): LiveData<List<ShowTimeModel>>

    @Query("SELECT COUNT(*) FROM recentShows WHERE releaseId = :releaseId")
    suspend fun getShowCount(releaseId: Int): Int

    @Query("DELETE FROM recentShows WHERE finishTimeStamp <= :timeStamp")
    suspend fun deleteShowOverTime(timeStamp: Long)

    @Query("SELECT * FROM recentShows WHERE releaseId = :releaseId")
    fun getShowData(releaseId : Int): ShowTimeModel

    @Query("DELETE FROM recentShows")
    suspend fun deleteAllShow()


}