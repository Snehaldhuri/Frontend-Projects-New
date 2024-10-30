package com.diipl.moviebeam.service.handler

import android.content.Context
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PreferenceHandler"
class PreferenceHandler(val context: Context) {

    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            context
        )
    }

    private val CONSTANT_IP = "0.0.0.0"

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

    var networkStatus = true
    var isGuestCheckedIn = true
    var isAdultDayPass = true

    var movieCount = 0
    var showsCount = 0
    var channelCount = 0

    var appList = ArrayList<String>()

    fun loadAllData() = CoroutineScope(Dispatchers.IO).launch {
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

        networkStatus = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.NETWORK_STATUS, false)
        isGuestCheckedIn = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.IS_GUEST_CHECKED_IN_KEY, false)
        isAdultDayPass = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS, false)

        movieCount = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.MOVIES_COUNT_KEY, 0)
        channelCount = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.CHANNEL_COUNT_KEY, 0)
        showsCount = preferenceDataStoreHelper.getFirstPreference(PreferenceDataStoreConstants.SHOWS_COUNT_KEY, 0)

        appList = ArrayList(preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.APP_LIST_KEY,
            emptySet()
        ))

    }

    fun updateAccountData(data : AccountSetupResponse){
        CoroutineScope(Dispatchers.IO).launch {
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
        connectivity: String? = null,
    ) {
        CoroutineScope(Dispatchers.IO).launch {

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