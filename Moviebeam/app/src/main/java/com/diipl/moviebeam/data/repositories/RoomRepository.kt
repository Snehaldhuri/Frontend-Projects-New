package com.diipl.moviebeam.data.repositories

import androidx.lifecycle.LiveData
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.room.dao.ProgramGuideDao
import com.diipl.moviebeam.room.db.MoviesDatabase
import com.diipl.moviebeam.room.models.RentalMovieModel
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val database: MoviesDatabase,
    private val programGuideDao: ProgramGuideDao
) {

    suspend fun insertRentalMovies(rentalMovieModel: RentalMovieModel) {
        database.movieDao().insertMovie(rentalMovieModel)
    }

    suspend fun updateRentalMovies(rentalMovieModel: RentalMovieModel) {
        database.movieDao().updateMovie(rentalMovieModel)
    }

    fun getWatchedMovies(): LiveData<List<RentalMovieModel>> {
        return database.movieDao().getMovies()
    }

    fun getRentalMovie(releaseId: Int): RentalMovieModel {
        return database.movieDao().getRentalMovie(releaseId)
    }

    suspend fun removeOverTimeMovies() {
        database.movieDao().deleteMovieOverTime(System.currentTimeMillis())
    }

    suspend fun deleteRecentMovies() {
        database.movieDao().deleteAllMovies()
    }

    suspend fun removeAllChannels() {
        programGuideDao.removeAllChannels()
    }

    suspend fun insertChannels(epgChannels: List<ChannelEpgDTO>) {
        programGuideDao.insertChannels(epgChannels)
    }

    fun getAllChannels(key: String?): LiveData<MutableList<ChannelEpgDTO>> {
        return programGuideDao.getChannels(key)
    }

}