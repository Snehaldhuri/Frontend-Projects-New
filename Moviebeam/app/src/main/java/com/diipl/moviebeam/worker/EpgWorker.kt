package com.diipl.moviebeam.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.logD
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@HiltWorker
class EpgWorker @AssistedInject constructor(
    private val movieBeamRepository: MovieBeamRepository,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        logD("doWork: Started")
        fetchEPGDataFromCloud()
        logD("doWork: Ended")
        return Result.success()
    }
    private suspend fun fetchEPGDataFromCloud() {
        movieBeamRepository.fetchEpgData()

        CoroutineScope(Dispatchers.Main).launch {
            movieBeamRepository.epgStatus.observeForever {
                when (it) {
                    EPGHandler.STATUS_OK -> {
                        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
                        this.cancel()
                    }
                }
            }
        }
    }

}