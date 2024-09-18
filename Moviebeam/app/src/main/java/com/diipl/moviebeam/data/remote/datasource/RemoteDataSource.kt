package com.diipl.moviebeam.data.remote.datasource

import android.R.attr.data
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.feedback.FeedbackResponse
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.movies.AdultDayPassRequest
import com.diipl.moviebeam.data.dto.movies.DayPassResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.dto.movies.RentalReversalRequest
import com.diipl.moviebeam.data.dto.movies.RentalReversalResponse
import com.diipl.moviebeam.data.dto.news.NewsHeaderResponse
import com.diipl.moviebeam.data.dto.news.NewsResponse
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.dto.sysInfo.SoftwareUpgradeResponse
import com.diipl.moviebeam.data.dto.sysInfo.SysInfoDTO
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.remote.services.AccountSetupApiService
import com.diipl.moviebeam.data.remote.services.AssetApiService
import com.diipl.moviebeam.data.remote.services.EpgApiService
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.data.remote.services.MoviesAPIService
import com.diipl.moviebeam.utils.ApiResponseParsing
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.NetworkHandler
import com.diipl.moviebeam.utils.NetworkUtils
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.toQueryMap
import javax.inject.Inject
import javax.inject.Named


private const val TAG = "RemoteDataSource"

class RemoteDataSource @Inject constructor(
    networkUtils: NetworkUtils,
    private val lgRestApiService: LgRestApiService,
    private val accountSetupApiService: AccountSetupApiService,
    @Named(Constants.ASSET) private val assetApiService: AssetApiService,
    @Named(Constants.SYS_INFO) private val sysInfoService: AssetApiService,
    private val moviesAPIService: MoviesAPIService,
    private val epgApiService: EpgApiService
) : NetworkHandler(networkUtils) {

    suspend fun getMoviesAccess(request: RentalMovieRequest): RentalMovieResponse? {
        val result = safeAPiCall { moviesAPIService.getRentalMovieAccess(request.toQueryMap()) }

        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, RentalMovieResponse::class)
        logD("In Rental Request Callback: " + "ERROR_CODE = " + responseObject?.errorCode + " RENTALID = " + responseObject?.rentalID)

        return responseObject
    }

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        val result = safeAPiCall {
            lgRestApiService.getWeather(ua)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, WeatherResponse::class)
        logD("In Weather Callback Success ")
        return responseObject
    }

    suspend fun getHotelServiceInfo(accountId: String): HotelServiceResponse? {
        val result = safeAPiCall {
            lgRestApiService.getHotelServices(accountId)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, HotelServiceResponse::class)
        logD("In Hotel Services Callback: version = "+ responseObject?.version)
        return responseObject
    }

    suspend fun getLocalAttractionInfo(ua: String): LocalAttractionResponse? {
        val result = safeAPiCall {
            lgRestApiService.getLocalAttraction(ua)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, LocalAttractionResponse::class)
        logD("In Local Attractions Callback: version = " + responseObject?.version)
        return responseObject
    }

    suspend fun getMoviesInfo(ua: String): MoviesResponse? {
        val result = safeAPiCall {
            lgRestApiService.getMovies(ua)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, MoviesResponse::class)
        logD("In Releases Callback: version = "+ responseObject?.version)
        return responseObject
    }

    suspend fun getShowtimeInfo(ua: String): ShowTimeResponse? {
        val result = safeAPiCall {
            lgRestApiService.getShowtime(ua)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, ShowTimeResponse::class)
        logD("In Showtime Releases Callback: version = "+responseObject?.version)
        return responseObject
    }

    suspend fun getThemeDetails(ua: String): ThemeResponse? {
        val result = safeAPiCall {
            lgRestApiService.getThemeDetails(ua)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, ThemeResponse::class)
        logD("In Theme Callback: version = "+responseObject?.version)
        return responseObject
    }

    suspend fun getDateTimeData(ua: String): DateTimeResponse? {
        val result = safeAPiCall {
            lgRestApiService.getDateTime(ua)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, DateTimeResponse::class)
        logD("In Date time callback")
        return responseObject
    }

    suspend fun getAccountSetupDetails(
        cmd: String, ua: String, mode: String
    ): AccountSetupResponse? {
        val result = safeAPiCall {
            accountSetupApiService.getAccountSetupDetails(cmd, ua, mode)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, AccountSetupResponse::class)
        logD("In Account Setup Callback ")
        return responseObject
    }

    suspend fun getFlightStatus(
        cmd: String, ua: String, callType: String, apCode: String, mode: String
    ): FlightStatusResponse? {
        val result = safeAPiCall {
            assetApiService.getFlightStatus(cmd, ua, callType, apCode, mode)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, FlightStatusResponse::class)
        logD("In Flight Status Callback")
        return responseObject
    }

    suspend fun getNewsHeader(ua: String, languageId: Int): NewsHeaderResponse? {
        val result = safeAPiCall {
            lgRestApiService.getNewsHeader(ua, languageId)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, NewsHeaderResponse::class)
        logD("In News Header Callback ")
        return responseObject
    }

    suspend fun getNewsDetails(newsId: Int): NewsResponse? {
        val result = safeAPiCall {
            lgRestApiService.getNewsDetails(newsId)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, NewsResponse::class)
        logD("In News Callback")
        return responseObject
    }


    suspend fun processStbMaster(
        ua: String,
        srNo: String,
        macAddress: String,
        wifiMacAddress: String,
        stbType: String
    ): StbMasterResponse? {
        val result = safeAPiCall {
            lgRestApiService.processStbMaster(
                ua,
                srNo,
                macAddress,
                wifiMacAddress,
                stbType
            )
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, StbMasterResponse::class)
        logD("In stbMaster Request Callback: ERROR_CODE = "+ responseObject?.errorCode)
        return responseObject
    }

    suspend fun sendGuestFeedback(
        ua: String, feedback: String, stbTime: String
    ): FeedbackResponse? {
        val result = safeAPiCall {
            lgRestApiService.sendGuestFeedback(ua, feedback, stbTime)
        }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, FeedbackResponse::class)
        logD("In Guest Feedback Callback")
        return responseObject
    }

    suspend fun laundryResponse(UA: String, serviceId: String): LaundryResponce? {
        val result = safeAPiCall { lgRestApiService.getLaundry(UA, serviceId) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, LaundryResponce::class)
        logD("In Laundry Releases Callback: version = "+responseObject?.version)
        return responseObject
    }

    suspend fun getChannelList(ua: String): ChannelListResponse? {
        val result = safeAPiCall { lgRestApiService.getChannelList(ua) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, ChannelListResponse::class)

        logD("In Channel List Callback, Total Channels "+responseObject?.channelLcnList?.size)
        return responseObject
    }

    suspend fun getEPGFromCloud(url: String): EPGResponse? {
        val result = safeAPiCall { epgApiService.getEPGFromCloud(url) }
        logD("In EPGFromCloud Callback Success ")

        return result.data
    }

    suspend fun getEPGDataFromServer(ua: String): EPGResponse? {
        val result = safeAPiCall { lgRestApiService.getEPGDataFromServer(ua) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, EPGResponse::class)
        logD("Fetching EPG data from server")
        return responseObject
    }

    suspend fun setRentalReversal(request: RentalReversalRequest): RentalReversalResponse? {
        val result = safeAPiCall { moviesAPIService.setRentalReversal(request.toQueryMap()) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, RentalReversalResponse::class)
        logD("In Rental Reversal Request Callback, Error Code "+responseObject?.errorCode)
        return responseObject
    }

    suspend fun buyPassRequest(request: AdultDayPassRequest): DayPassResponse? {
        val result = safeAPiCall { moviesAPIService.buyPassRequest(request.toQueryMap()) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, DayPassResponse::class)
        logD("In buyPassRequest Callback Success ")
        return responseObject
    }

    suspend fun sendSysInfo(ua: String, body: SysInfoDTO): Int? {
        val result = safeAPiCall { sysInfoService.sendSysInfo(ua, body) }
        val responseObject = ApiResponseParsing().parseSysInfoResponse(result.data)
        logD("SYSINFO Callback Success ")
        return responseObject
    }

    suspend fun getSoftwareUpdateDetails(stbTypeId: Int, ua: String): SoftwareUpgradeResponse? {
        val result = safeAPiCall { lgRestApiService.getSoftwareUpdateDetails(stbTypeId, ua) }
        return ApiResponseParsing().getResponseAsObject(result.data, SoftwareUpgradeResponse::class)
    }

    suspend fun getTvTickerMessages(ua: String): TickerResponse? {
        val result = safeAPiCall { lgRestApiService.getTvTickerMessages(ua) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, TickerResponse::class)
        logD("In TickerModel callback Success ")
        return responseObject
    }

    suspend fun getGuestMessages(ua: String, guestSessionId: String): MessageResponse? {
        val result = safeAPiCall { lgRestApiService.getGuestMessages(ua, guestSessionId) }
        val responseObject = ApiResponseParsing().getResponseAsObject(result.data, MessageResponse::class)
        val messageCount = responseObject?.messagesList?.size ?: 0

        logD("In Message Callback, new messages count = $messageCount")
        return responseObject

    }

}