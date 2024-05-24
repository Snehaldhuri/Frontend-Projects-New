package com.diipl.moviebeam.ui.refreshingui

/*import android.content.Context
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateDataWorker @AssistedInject constructor(
    private val moviesDataStore: DataStore<MoviesResponse>,
    private val showTimeDataStore: DataStore<ShowTimeResponse>,
    private val weatherDataStore: DataStore<WeatherResponse>,
    private val localAttractionDataStore: DataStore<LocalAttractionResponse>,
    private val hotelServiceDataStore: DataStore<HotelServiceResponse>,
    private val movieBeamRepository: MovieBeamRepository,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val isMovie = inputData.getBoolean("isMovie", false)

      try {

            return Result.success()
        } catch (e: Exception) {

            return Result.failure()
        }

    }


}*/
