package com.diipl.moviebeam.service

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
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.isEpgDataValid
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.removeEarlierData
import com.diipl.moviebeam.utils.toInteger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    private var isEPGServerApiCalled = false
    private var ua = ""

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

    private suspend fun fetchEPGDataFromServer() {
        val response = movieBeamRepository.getEPGDataFromServer(ua)
        if (response != null) {
            logD("fetchEPGDataFromServer: Success")
            handleEpgResponse(response)
        } else {
            logE("fetchEPGDataFromServer: Failed")
        }
    }

    private suspend fun handleEpgResponse(response: EPGResponse?) {
        //Removing all Epg Channels From RoomDB.
        roomRepository.removeAllChannels()

        val simpleDateFormatter = SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
        response?.let {
            if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                logD("handleEpgResponse: Valid Epg Data Found")
                updateEpgStandEt(it.ST, it.ET)
                val currentKey = fetchCurrentProgramKey()
                removeEarlierData(it.epgListMap?.entries?.iterator(), currentKey)
                for (entries in it.epgListMap?.entries!!) {
                    val iterator = entries.value.iterator()
                    val key = entries.key
                    val ciMap = HashMap<Int, Boolean>()
                    while (iterator.hasNext()) {
                        val channel = iterator.next()
                        channel.key = key
                        if (channel.CI != null) {
                            var isFound = false
                            for (channelApi in channelList) {
                                if (channelApi.CI == channel.CI) {
                                    isFound = true
                                    //Mapping EpgMap with Channel List Api
                                    channel.AR = channelApi.AR
                                    channel.CN = channelApi.CN
                                    channel.CNO = channelApi.CNO
                                    channel.CBT = channelApi.CBT
                                    channel.CL = channelApi.CL
                                    channel.CLCloud = channelApi.CLCloud
                                    if (channelApi.httpStreaming == true)
                                        channel.VP = channelApi.httpStreamingUrl
                                    else
                                        channel.VP = channelApi.VP
                                    channel.param1 = channelApi.param1
                                    channel.param2 = channelApi.param2
                                    channel.httpStreamingUrl = channelApi.httpStreamingUrl
                                    channel.httpStreaming = channelApi.httpStreaming
                                    channel.recordable = channelApi.recordable

                                    channel.channelNameNo =
                                        "${channelApi.CNO}   ${channelApi.CN}"
                                    channel.lastProg = channel.C
                                    channel.prog1Time =
                                        "${channel.P1_ST} - ${channel.P1_ET}"

                                    //Mapping EpgMap with Program Map Api
                                    if (channel.P1_ID != null) {
                                        val program1 =
                                            it.programsListMap?.get(channel.P1_ID)
                                        if (program1 != null) {
                                            channel.P1_PT = program1.PT
                                            channel.P1_SY = program1.SY
                                            channel.progInfo = program1.PT
                                            channel.progSynopsis = program1.SY
                                            channel.liveProg1 = program1.PT
                                            channel.progInfo1 =
                                                "${channel.CNO} - ${program1.PT}"
                                        } else {
                                            channel.P1_PT =
                                                Constants.NO_INFORMATION_AVAILABLE
                                            channel.P1_SY =
                                                Constants.NO_INFORMATION_AVAILABLE
                                            channel.progInfo =
                                                Constants.NO_INFORMATION_AVAILABLE
                                            channel.progSynopsis =
                                                Constants.NO_INFORMATION_AVAILABLE
                                            channel.liveProg1 =
                                                Constants.NO_INFORMATION_AVAILABLE
                                        }
                                    }
                                    // for live tv and full screen (Next)
                                    if (channel.C?.toInteger()!! > 1) {
                                        if (channel.P2_ID != null) {
                                            val program2 =
                                                it.programsListMap?.get(channel.P2_ID)
                                            channel.P2_PT = program2?.PT
                                            channel.P2_SY = program2?.SY
                                            channel.liveProg2 = program2?.PT
                                            channel.progInfo2 =
                                                "${channel.CNO} - ${program2?.PT}"
                                            channel.prog2Time =
                                                "${channel.P2_ST} - ${channel.P2_ET}"
                                        }
                                        if (channel.P3_ID != null) {
                                            val program3 =
                                                it.programsListMap?.get(channel.P3_ID)
                                            channel.P3_PT = program3?.PT
                                            channel.P3_SY = program3?.SY
                                        }
                                        if (channel.P4_ID != null) {
                                            val program4 =
                                                it.programsListMap?.get(channel.P4_ID)
                                            channel.P4_PT = program4?.PT
                                            channel.P4_SY = program4?.SY
                                        }
                                        if (channel.P5_ID != null) {
                                            val program5 =
                                                it.programsListMap?.get(channel.P5_ID)
                                            channel.P5_PT = program5?.PT
                                            channel.P5_SY = program5?.SY
                                        }
                                        if (channel.P6_ID != null) {
                                            val program6 =
                                                it.programsListMap?.get(channel.P6_ID)
                                            channel.P6_PT = program6?.PT
                                            channel.P6_SY = program6?.SY
                                        }
                                        if (channel.P7_ID != null) {
                                            val program7 =
                                                it.programsListMap?.get(channel.P7_ID)
                                            channel.P7_PT = program7?.PT
                                            channel.P7_SY = program7?.SY
                                        }
                                        if (channel.P8_ID != null) {
                                            val program8 =
                                                it.programsListMap?.get(channel.P8_ID)
                                            channel.P8_PT = program8?.PT
                                            channel.P8_SY = program8?.SY
                                        }
                                    } else {
                                        //Calculating next Program Time from program1 end Time when Only One Program is Available
                                        val nextProgramTime = Calendar.getInstance()
                                        nextProgramTime.time =
                                            simpleDateFormatter.parse(channel.P1_DET)
                                        val nextProgramKey =
                                            fetchCurrentProgramKey(nextProgramTime)
                                        val nextProgram: ChannelEpgDTO? =
                                            it.epgListMap[nextProgramKey]?.first {
                                                it.CI == channelApi.CI
                                            }
                                        when (nextProgramTime.get(Calendar.MINUTE)) {
                                            0, 30 -> {
                                                channel.prog2Time =
                                                    "${nextProgram?.P1_ST} - ${channel.P1_ET}"
                                                channel.liveProg2 =
                                                    it.programsListMap?.get(nextProgram?.P1_ID)?.PT
                                            }

                                            else -> {
                                                channel.prog2Time =
                                                    "${channel.P2_ST} - ${channel.P2_ET}"
                                                channel.liveProg2 =
                                                    it.programsListMap?.get(nextProgram?.P2_ID)?.PT
                                            }
                                        }
                                    }
                                    break
                                }
                            }
                            if (!isFound)
                                iterator.remove()
                            else {
                                //Removing Duplicate Channels
                                if (ciMap[channel.CI] != null)
                                    iterator.remove()
                                else
                                    ciMap[channel.CI] = true
                            }
                        }
                    }
                    //Sorting Channels by Channel No
                    entries.value.sortBy { it.CNO?.toInteger() }
                    //Adding Channels to RoomDB.
                    roomRepository.insertChannels(entries.value)
                }
            } else {
                logE("handleEpgResponse: Invalid Epg Data")
                if (!isEPGServerApiCalled) {
                    isEPGServerApiCalled = true
                    logD("handleEpgResponse: Calling Epg Server Api")
                    fetchEPGDataFromServer()
                }
            }
            logD("handleEpgResponse: Execution Done")
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