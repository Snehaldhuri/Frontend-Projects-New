package com.diipl.moviebeam.service.handler

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.isEpgDataValid
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.removeEarlierData
import com.diipl.moviebeam.utils.toInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EPGHandler(val context: Context, val roomRepository: RoomRepository) {

    private val TAG = "EPGHandler"

    private val preferenceHandler by lazy { PreferenceHandler(context) }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var _epgStatus = MutableLiveData(STATUS_INIT)
    val epgStatus : LiveData<Int> get() = _epgStatus

    companion object {
        const val STATUS_OK = 0
        const val STATUS_FAIL = 1
        const val STATUS_INIT = -1
    }

    fun parseEPG(epgResponse: EPGResponse, channelList : MutableList<ChannelEpgDTO>) = coroutineScope.launch {
        _epgStatus.postValue(STATUS_INIT)

        roomRepository.removeAllChannels()
        Log.e(TAG, "parseEPG:  data removed")
        val simpleDateFormatter = SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)

        if (isEpgDataValid(epgResponse.ST, epgResponse.ET, simpleDateFormatter)) {
            logD("Valid EPG data found EPG Start time: ${epgResponse.ST} & EPG End time: ${epgResponse.ET}")
            preferenceHandler.updateDatastoreVariables(
                epgStartTime = epgResponse.ST,
                epgEndTime = epgResponse.ET
            )

            val currentKey = fetchCurrentProgramKey()

            epgResponse.epgListMap?.entries?.let {map->
                removeEarlierData(map.iterator(), currentKey)
                map.forEachIndexed { index, entries ->
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
                                        val program1 = epgResponse.programsListMap?.get(channel.P1_ID)
                                        if (program1 != null) {
                                            channel.P1_PT = program1.PT
                                            channel.P1_SY = program1.SY
                                            channel.progInfo = program1.PT
                                            channel.progSynopsis = program1.SY
                                            channel.liveProg1 = program1.PT
                                            channel.progInfo1 = "${channel.CNO} - ${program1.PT}"
                                        } else {
                                            channel.P1_PT = Constants.NO_INFORMATION_AVAILABLE
                                            channel.P1_SY = Constants.NO_INFORMATION_AVAILABLE
                                            channel.progInfo = Constants.NO_INFORMATION_AVAILABLE
                                            channel.progSynopsis = Constants.NO_INFORMATION_AVAILABLE
                                            channel.liveProg1 = Constants.NO_INFORMATION_AVAILABLE
                                        }
                                    }
                                    // for live tv and full screen (Next)
                                    if (channel.C?.toInteger()!! > 1) {
                                        if (channel.P2_ID != null) {
                                            val program2 = epgResponse.programsListMap?.get(channel.P2_ID)
                                            if (program2 != null) {
                                                channel.P2_PT = program2.PT
                                                channel.P2_SY = program2.SY
                                                channel.liveProg2 = program2.PT
                                                channel.progInfo2 = "${channel.CNO} - ${program2.PT}"
                                                channel.prog2Time = "${channel.P2_ST} - ${channel.P2_ET}"
                                            } else {
                                                channel.P2_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P2_SY = Constants.NO_INFORMATION_AVAILABLE
                                                channel.progInfo = Constants.NO_INFORMATION_AVAILABLE
                                                channel.progSynopsis = Constants.NO_INFORMATION_AVAILABLE
                                                channel.liveProg2 = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        if (channel.P3_ID != null) {
                                            val program3 = epgResponse.programsListMap?.get(channel.P3_ID)
                                            if (program3 != null){
                                                channel.P3_PT = program3.PT
                                                channel.P3_SY = program3.SY
                                            } else {
                                                channel.P3_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P3_SY = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        if (channel.P4_ID != null) {
                                            val program4 = epgResponse.programsListMap?.get(channel.P4_ID)
                                            if (program4 != null) {
                                                channel.P4_PT = program4.PT
                                                channel.P4_SY = program4.SY
                                            } else {
                                                channel.P4_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P4_SY = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        if (channel.P5_ID != null) {
                                            val program5 = epgResponse.programsListMap?.get(channel.P5_ID)
                                            if (program5 != null) {
                                                channel.P5_PT = program5.PT
                                                channel.P5_SY = program5.SY
                                            } else {
                                                channel.P5_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P5_SY = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        if (channel.P6_ID != null) {
                                            val program6 = epgResponse.programsListMap?.get(channel.P6_ID)
                                            if (program6 != null) {
                                                channel.P6_PT = program6.PT
                                                channel.P6_SY = program6.SY
                                            } else {
                                                channel.P6_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P6_SY = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        if (channel.P7_ID != null) {
                                            val program7 = epgResponse.programsListMap?.get(channel.P7_ID)
                                            if (program7 != null) {
                                                channel.P7_PT = program7.PT
                                                channel.P7_SY = program7.SY
                                            } else {
                                                channel.P7_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P7_SY = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                        if (channel.P8_ID != null) {
                                            val program8 = epgResponse.programsListMap?.get(channel.P8_ID)
                                            if (program8 != null) {
                                                channel.P8_PT = program8.PT
                                                channel.P8_SY = program8.SY
                                            } else {
                                                channel.P8_PT = Constants.NO_INFORMATION_AVAILABLE
                                                channel.P8_SY = Constants.NO_INFORMATION_AVAILABLE
                                            }
                                        }
                                    } else {
                                        //Calculating next Program Time from program1 end Time when Only One Program is Available
                                        val nextProgramTime = Calendar.getInstance()
                                        nextProgramTime.time = simpleDateFormatter.parse(channel.P1_DET)!!
                                        val nextProgramKey =
                                            fetchCurrentProgramKey(nextProgramTime)
                                        val nextProgram: ChannelEpgDTO? =
                                            epgResponse.epgListMap[nextProgramKey]?.first {
                                                it.CI == channelApi.CI
                                            }
                                        when (nextProgramTime.get(Calendar.MINUTE)) {
                                            0, 30 -> {
                                                channel.prog2Time = "${nextProgram?.P1_ST} - ${channel.P1_ET}"
                                                channel.liveProg2 = epgResponse.programsListMap?.get(nextProgram?.P1_ID)?.PT
                                            }

                                            else -> {
                                                channel.prog2Time = "${channel.P2_ST} - ${channel.P2_ET}"
                                                channel.liveProg2 = epgResponse.programsListMap?.get(nextProgram?.P2_ID)?.PT
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
                    entries.value.sortBy {no -> no.CNO?.toInteger() }
                    //Adding Channels to RoomDB.
                    coroutineScope.launch(Dispatchers.IO) {
                        roomRepository.insertChannels(entries.value)
                    }
                    if (index == map.size-1){
                        Log.e(TAG, "parseEPG: $index  ${map.size-1}")
                        preferenceHandler.updateDatastoreVariables(isEPGEmpty = true)
                        _epgStatus.postValue(STATUS_OK)
                    }
                }
            }

        } else {
            preferenceHandler.updateDatastoreVariables(isEPGEmpty = false)
            logE("Invalid EPG data found")
            _epgStatus.postValue(STATUS_FAIL)
        }
    }

}