package com.diipl.moviebeam.worker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.repositories.MovieBeamRepository
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "EpgWorker"

@HiltWorker
class EpgWorker @AssistedInject constructor(
    private val movieBeamRepository: MovieBeamRepository,
    private val roomRepository: RoomRepository,
    private val channelListDataStore: DataStore<ChannelListResponse>,
    private val preferenceHandler: PreferenceHandler,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private var channelList: MutableList<ChannelEpgDTO> = mutableListOf()
    private var ua = ""
    private val epgHandler by lazy { EPGHandler(appContext, roomRepository) }

    override suspend fun doWork(): Result {
        logD("doWork: Started")
        initializeDatastoreParams()
        logD("doWork: Ended")
        return Result.success()
    }

    private fun getHotelCustomizationResponseData(dataStore: DataStore<ChannelListResponse>) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.collect {
                channelList = it.channelLcnList
                fetchEPGDataFromCloud()
            }
        }
    }

    private suspend fun fetchEPGDataFromCloud() {
        val response = movieBeamRepository.getEPGFromCloud(preferenceHandler.epgCDNUrl)
        if (response != null) {
            logD("fetchEPGDataFromCloud: Success")
            handleEpgResponse(response)
        } else {
            logE("fetchEPGDataFromCloud: Failed")
            fetchEPGDataFromServer()
        }
    }

    private fun fetchEPGDataFromServer() = CoroutineScope(Dispatchers.IO).launch {
        val response = movieBeamRepository.getEPGDataFromServer(ua)
        if (response != null) {
            logD("fetchEPGDataFromServer: Success")
            handleEpgResponse(response, true)
        } else {
            logE("fetchEPGDataFromServer: Failed")
        }
    }

    private fun handleEpgResponse(response: EPGResponse?, isCDN : Boolean = false)  {
       response?.let { data ->
           epgHandler.parseEPG(data, channelList)

           GlobalScope.launch(Dispatchers.Main) {
               epgHandler.epgStatus.observeForever {
                   when (it) {
                       EPGHandler.STATUS_FAIL -> {
                           if (!isCDN) {
                               fetchEPGDataFromServer()
                           }
                           this.cancel()
                       }

                       EPGHandler.STATUS_OK -> this.cancel()
                   }
               }
           }
       }
    }

    private fun initializeDatastoreParams() = CoroutineScope(Dispatchers.Default).launch {
        preferenceHandler.loadAllData()
        delay(100)
        ua = preferenceHandler.UA
        getHotelCustomizationResponseData(channelListDataStore)
    }

    private fun updateEpgStandEt(epgStartTime: String?, epgEndTime: String?) {
        epgStartTime?.let {
            preferenceHandler.updateDatastoreVariables(epgStartTime = it)
        }
        epgEndTime?.let {
            preferenceHandler.updateDatastoreVariables(epgEndTime = it)
        }
    }

}