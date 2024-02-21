package com.diipl.moviebeam.data.repositories

import androidx.lifecycle.LiveData
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.room.db.MoviesDatabase
import com.diipl.moviebeam.room.models.RentalMovieModel
import com.diipl.moviebeam.room.models.ShowTimeModel
import javax.inject.Inject

class RoomRepository @Inject constructor(private val database: MoviesDatabase) {

    suspend fun insertRentalMovies(rentalMovieModel: RentalMovieModel) {
        rentalMovieModel.movieData?.let {
            if (database.movieDao().getMovieCount(it.releaseId) == 0  && Constants.SESSION_ID.isNotEmpty() && Constants.SESSION_ID != "null"){
                database.movieDao().insertMovie(rentalMovieModel)
            }
        }
    }

    suspend fun updateRentalMovies(rentalMovieModel: RentalMovieModel) {
        rentalMovieModel.movieData?.let {
            if (database.movieDao().getMovieCount(it.releaseId) != 0  && Constants.SESSION_ID.isNotEmpty() && Constants.SESSION_ID != "null"){
                database.movieDao().updateMovie(rentalMovieModel)
            }
        }
    }

    suspend fun updateShowDetails(showTimeModel: ShowTimeModel) {
        showTimeModel.seriesData?.let {
            if (database.showDao().getShowCount(it.releaseId) != 0 && Constants.SESSION_ID.isNotEmpty()){
                database.showDao().updateShow(showTimeModel)
            }
        }

    }

    suspend fun insertShowDetails(showTimeModel: ShowTimeModel) {
        showTimeModel.seriesData?.let {
            if (database.showDao().getShowCount(it.releaseId) == 0 && Constants.SESSION_ID.isNotEmpty()){
                database.showDao().insertShow(showTimeModel)
            }
        }
    }

    fun getWatchedMovies(): LiveData<List<RentalMovieModel>> {
        return database.movieDao().getMovies()
    }

    fun getRentalMovie(releaseId : Int): RentalMovieModel {
        return database.movieDao().getRentalMovie(releaseId)
    }

    suspend fun removeOverTimeMovies() {
        database.movieDao().deleteMovieOverTime(System.currentTimeMillis())
    }

    suspend fun deleteRecentMovies() {
        database.movieDao().deleteAllMovies()
    }

    fun getShowData(releaseId: Int): ShowTimeModel {
        return database.showDao().getShowData(releaseId)
    }

    suspend fun deleteRecentShows() {
        database.showDao().deleteAllShow()
    }

    suspend fun deleteShowDetails(seriesData: ShowTimeModel) {
        database.showDao().deleteShow(seriesData)
    }

    suspend fun deleteMovieDetails(movieData: RentalMovieModel) {
        database.movieDao().deleteMovie(movieData)
    }


}