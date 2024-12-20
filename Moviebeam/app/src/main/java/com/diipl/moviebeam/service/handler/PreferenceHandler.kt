package com.diipl.moviebeam.service.handler

import android.content.Context
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.remote.RemoteModel
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.isNotEmptyOrNull
import com.diipl.moviebeam.utils.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "PreferenceHandler"
class PreferenceHandler(val context: Context) {

    private val preferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            context
        )
    }
    private val preferences by lazy { SharedPreference(context) }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    val CONSTANT_IP = "0.0.0.0"

    var accountID = ""
    var roomNo = ""
    var serialNo = ""
    var UA = ""
    var ipAddress = ""
    var hotelVideoUrl = ""
    var gradientStartColor = ""
    var gradientEndColor = ""
    var castingUrl = ""
    var epgCDNUrl = ""
    var epgStartTime = ""
    var epgEndTime = ""
    var netMask = ""
    var gatewayIP = ""
    var sessionId = ""
    var cListVersion = ""
    var connectivity = ""
    var laVersion = ""
    var hsVersion = ""
    var themeVersion = ""
    var moviesVersion = ""
    var showTimeVersion = ""

    var networkStatus = true
    var isGuestCheckedIn = true
    var isAdultDayPass = true
    var isStbAllocated = false
    var isContentDetailFlagEnabled = true
    var isAllDataFetched = false
    var isStbRegistered = false
    var isHSEmpty = true
    var isLAEmpty = true
    var isThemeEmpty = true
    var isMoviesEmpty = true
    var isShowtimeEmpty = true
    var isEPGEmpty = true

    var movieCount = 0
    var showsCount = 0
    var channelCount = 0

    var appList = ArrayList<String>()

    var remoteModel = RemoteModel()

    init {
        job?.cancel()
        job = null
        job = coroutineScope.launch {
            while (isActive){
                loadAllData()
                delay(1000*5)
            }
        }
    }

    private fun loadAllData() = coroutineScope.launch {
        accountID = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.ACCOUNT_ID_KEY, "")
        roomNo = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.STB_ROOM_NO_KEY, "")
        serialNo = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.SERIAL_NO, "0")
        UA = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.UA, "")
        ipAddress = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IP_ADDRESS_KEY, CONSTANT_IP)
        hotelVideoUrl = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.HOTEL_VIDEO_URL_KEY, "")
        gradientStartColor = preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
            Constants.DEFAULTGRADIENTSTARTCOLOR
        )
        gradientEndColor = preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
            Constants.DEFAULTGRADIENTENDCOLOR
        )
        castingUrl = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.CASTING_URL_KEY, "")
        epgCDNUrl = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.EPG_CDN_URL_KEY, "")
        epgStartTime = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.EPG_START_TIME_KEY, "")
        epgEndTime = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.EPG_END_TIME_KEY, "")
        netMask = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IP_NET_MASK_KEY, CONSTANT_IP)
        gatewayIP = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IP_GATEWAY_KEY, CONSTANT_IP)
        sessionId = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.SESSION_ID_KEY, "")
        cListVersion = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.C_LIST_VERSION_KEY, "")
        connectivity = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.CONNECTIVITY_KEY, "NO INTERNET")
        laVersion = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.LA_VERSION_KEY, "0")
        hsVersion = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.HS_VERSION_KEY, "0")
        themeVersion = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.THEME_VERSION_KEY, "0")
        moviesVersion = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.MOVIES_VERSION_KEY, "0")
        showTimeVersion = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.SHOW_TIME_VERSION_KEY, "0")

        networkStatus = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.NETWORK_STATUS, false)
        isGuestCheckedIn = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY, false)
        isAdultDayPass = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS, false)
        isAllDataFetched = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_ALL_DATA_FETCHED, false)
        isContentDetailFlagEnabled = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.CONTENT_DETAIL_FLAG, false)
        isStbAllocated = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_STB_ALLOCATED, false)
        isStbRegistered = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_STB_REGISTERED, false)
        isLAEmpty = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_LA_EMPTY, true)
        isHSEmpty = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_HS_EMPTY, true)
        isThemeEmpty = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_THEME_EMPTY, true)
        isMoviesEmpty = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_MOVIES_EMPTY, true)
        isShowtimeEmpty = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_SHOWTIME_EMPTY, true)
        isEPGEmpty = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_EPG_EMPTY, true)

        movieCount = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.MOVIES_COUNT_KEY, 0)
        channelCount = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.CHANNEL_COUNT_KEY, 0)
        showsCount = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.SHOWS_COUNT_KEY, 0)

        appList = ArrayList(preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.APP_LIST_KEY,
            emptySet()
        ))

        remoteModel = preferences.btRemoteModel

    }

    fun updateAccountData(data : AccountSetupResponse){
        coroutineScope.launch {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.CONTENT_DETAIL_FLAG,
                data.contentDetailFlag
            )
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.ACCOUNT_ID_KEY,
                data.accountId
            )
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
                data.roomNo
            )
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.EPG_CDN_URL_KEY,
                data.epgCdnUrl + data.accountId + Constants.EPG_CLOUD_URL_SUFFIX
            )
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.CASTING_URL_KEY,
                data.stbCastingPageUrl
            )
            if (data.contentDetailFlag)
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.HOTEL_VIDEO_URL_KEY,
                    data.httpStreamingHotelvideoUrl + data.hotelChannelList[0].fileName
                )

            val  isNotNull = data.tvRemoteProtocol.isNotEmptyOrNull() && data.dthRemoteProtocol.isNotEmptyOrNull() &&
                    data.sourceHdmiCode.isNotEmptyOrNull() && data.destHdmiCode.isNotEmptyOrNull()

            val msg = "tvBrandId:${data.tvBrandId}, tvDthId:${data.tvDthId}, sourceHdmiId:${data.sourceHdmiId}, " +
                    "destHdmiId:${data.destHdmiId}, sourceHdmiCode:${data.sourceHdmiCode}, destHdmiCode:${data.destHdmiCode}, " +
                    "tvRemoteProtocol:${data.tvRemoteProtocol}, dthRemoteProtocol:${data.dthRemoteProtocol}"

            this@PreferenceHandler.logD("Remote Details:->> $msg ")

            if (isNotNull){
                val model = RemoteModel(
                    tvId = data.tvBrandId,
                    dthId = data.tvDthId,
                    sourceId = data.sourceHdmiId,
                    destinationId = data.destHdmiId,
                    sourceCode = data.sourceHdmiCode,
                    destinationCode = data.destHdmiCode,
                    tvProtocol = data.tvRemoteProtocol,
                    dthProtocol = data.dthRemoteProtocol
                )
                preferences.btRemoteModel = model
                remoteModel = model
            }
        }
    }

    fun updateDatastoreVariables(
        moviesCount: Int? = null,
        showsCount: Int? = null,
        cListVersion: String? = null,
        gradientStartColor: String? = null,
        gradientEndColor: String? = null,
        channelCount: Int? = null,
        epgStartTime: String? = null,
        epgEndTime: String? = null,
        sessionId: String? = null,
        isCheckedIn: Boolean? = null,
        isStbAllocated: Boolean? = null,
        appList: Set<String>? = null,
        ipAddress: String? = null,
        netMask: String? = null,
        gateway: String? = null,
        isAllDataFetched: Boolean? = null,
        isStbRegistered: Boolean? = null,
        connectivity: String? = null,
        laVersion: String? = null,
        hsVersion: String? = null,
        themeVersion: String? = null,
        moviesVersion: String? = null,
        showTimeVersion: String? = null,
        isHSEmpty: Boolean? = null,
        isLAEmpty: Boolean? = null,
        isThemeEmpty: Boolean? = null,
        isMoviesEmpty: Boolean? = null,
        isShowtimeEmpty: Boolean? = null,
        isEPGEmpty: Boolean? = null,
    ) {
        coroutineScope.launch {
            isEPGEmpty?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_HS_EMPTY, it
                )
            }
            isHSEmpty?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_HS_EMPTY, it
                )
            }
            isLAEmpty?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_LA_EMPTY, it
                )
            }
            isThemeEmpty?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_THEME_EMPTY, it
                )
            }
            isMoviesEmpty?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_MOVIES_EMPTY, it
                )
            }
            isShowtimeEmpty?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_SHOWTIME_EMPTY, it
                )
            }
            laVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.LA_VERSION_KEY, it
                )
            }
            hsVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.HS_VERSION_KEY, it
                )
            }
            themeVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.THEME_VERSION_KEY, it
                )
            }
            moviesVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.MOVIES_VERSION_KEY, it
                )
            }
            showTimeVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SHOW_TIME_VERSION_KEY, it
                )
            }
            isStbRegistered?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_STB_REGISTERED, it
                )
            }

            isAllDataFetched?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_ALL_DATA_FETCHED, it
                )
            }

            isStbAllocated?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_STB_ALLOCATED, it
                )
            }

            isCheckedIn?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY, it
                )
            }
            sessionId?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SESSION_ID_KEY, it
                )
            }
            moviesCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.MOVIES_COUNT_KEY,
                    it
                )
            }
            showsCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.SHOWS_COUNT_KEY,
                    it
                )
            }
            cListVersion?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.C_LIST_VERSION_KEY,
                    it
                )
            }
            gradientStartColor?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
                    it
                )
            }
            gradientEndColor?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
                    it
                )
            }
            channelCount?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.CHANNEL_COUNT_KEY,
                    it
                )
            }
            epgStartTime?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.EPG_START_TIME_KEY,
                    it
                )
            }
            epgEndTime?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.EPG_END_TIME_KEY,
                    it
                )
            }

            appList?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.APP_LIST_KEY,
                    it)
            }

            ipAddress?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IP_ADDRESS_KEY,
                    it
                )
            }
            netMask?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IP_NET_MASK_KEY,
                    it
                )
            }
            gateway?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.IP_GATEWAY_KEY,
                    it
                )
            }
            connectivity?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.CONNECTIVITY_KEY,
                    it
                )
            }
        }
    }

}