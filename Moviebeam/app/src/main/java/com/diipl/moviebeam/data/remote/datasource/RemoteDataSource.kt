package com.diipl.moviebeam.data.remote.datasource

import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.feedback.FeedbackResponse
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
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
import com.diipl.moviebeam.data.dto.sysInfo.SoftwareResponseDTO
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
        return ApiResponseParsing().getResponseAsObject(result.data, RentalMovieResponse::class)
    }

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        val result = safeAPiCall {
            lgRestApiService.getWeather(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, WeatherResponse::class)
    }

    suspend fun getHotelServiceInfo(accountId: String): HotelServiceResponse? {
        val result = safeAPiCall {
            lgRestApiService.getHotelServices(accountId)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, HotelServiceResponse::class)
    }

    suspend fun getLocalAttractionInfo(ua: String): LocalAttractionResponse? {
        val result = safeAPiCall {
            lgRestApiService.getLocalAttraction(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, LocalAttractionResponse::class)
    }

    suspend fun getMoviesInfo(ua: String): MoviesResponse? {
        val result = safeAPiCall {
            lgRestApiService.getMovies(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, MoviesResponse::class)
    }

    suspend fun getShowtimeInfo(ua: String): ShowTimeResponse? {
        val result = safeAPiCall {
            lgRestApiService.getShowtime(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, ShowTimeResponse::class)
    }

    suspend fun getThemeDetails(ua: String): ThemeResponse? {
        val result = safeAPiCall {
            lgRestApiService.getThemeDetails(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, ThemeResponse::class)
    }

    suspend fun getDateTimeData(ua: String): DateTimeResponse? {
        val result = safeAPiCall {
            lgRestApiService.getDateTime(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, DateTimeResponse::class)
    }

    suspend fun getAccountSetupDetails(
        cmd: String, ua: String, mode: String
    ): AccountSetupResponse? {
        val result = safeAPiCall {
            accountSetupApiService.getAccountSetupDetails(cmd, ua, mode)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, AccountSetupResponse::class)
    }

    suspend fun getFlightStatus(
        cmd: String, ua: String, callType: String, apCode: String, mode: String
    ): FlightStatusResponse? {
        val result = safeAPiCall {
            assetApiService.getFlightStatus(cmd, ua, callType, apCode, mode)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, FlightStatusResponse::class)
    }

    suspend fun getNewsHeader(ua: String, languageId: Int): NewsHeaderResponse? {
        val result = safeAPiCall {
            lgRestApiService.getNewsHeader(ua, languageId)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, NewsHeaderResponse::class)
    }

    suspend fun getNewsDetails(newsId: Int): NewsResponse? {
        val result = safeAPiCall {
            lgRestApiService.getNewsDetails(newsId)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, NewsResponse::class)
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
        return ApiResponseParsing().getResponseAsObject(result.data, StbMasterResponse::class)
    }

    suspend fun sendGuestFeedback(
        ua: String, feedback: String, stbTime: String
    ): FeedbackResponse? {
        val result = safeAPiCall {
            lgRestApiService.sendGuestFeedback(ua, feedback, stbTime)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, FeedbackResponse::class)
    }

    suspend fun laundryResponse(UA: String, serviceId: String): LaundryResponce? {
        val result = safeAPiCall { lgRestApiService.getLaundry(UA, serviceId) }
        return ApiResponseParsing().getResponseAsObject(result.data, LaundryResponce::class)
    }

    suspend fun getChannelList(ua: String): ChannelListResponse? {
        val result = safeAPiCall { lgRestApiService.getChannelList(ua) }
        return ApiResponseParsing().getResponseAsObject(result.data, ChannelListResponse::class)
    }

    suspend fun getEPGFromCloud(url: String): EPGResponse? {
        val result = safeAPiCall { epgApiService.getEPGFromCloud(url) }
        return result.data
    }

    suspend fun getEPGDataFromServer(ua: String): EPGResponse? {
        val result = safeAPiCall { lgRestApiService.getEPGDataFromServer(ua) }
        return ApiResponseParsing().getResponseAsObject(result.data, EPGResponse::class)
    }

    suspend fun setRentalReversal(request: RentalReversalRequest): RentalReversalResponse? {
        val result = safeAPiCall { moviesAPIService.setRentalReversal(request.toQueryMap()) }
        return ApiResponseParsing().getResponseAsObject(result.data, RentalReversalResponse::class)
    }

    suspend fun buyPassRequest(request: AdultDayPassRequest): DayPassResponse? {
        val result = safeAPiCall { moviesAPIService.buyPassRequest(request.toQueryMap()) }
        return ApiResponseParsing().getResponseAsObject(result.data, DayPassResponse::class)
    }

    suspend fun sendSysInfo(ua: String, body: SysInfoDTO): Int? {
        val result = safeAPiCall { sysInfoService.sendSysInfo(ua, body) }
        return ApiResponseParsing().parseSysInfoResponse(result.data)
    }

    suspend fun getSoftwareUpdateDetails(): SoftwareResponseDTO? {
        val result = safeAPiCall { epgApiService.getSoftwareUpdateDetails() }
        return result.data
    }

    suspend fun getTvTickerMessages(ua: String): TickerResponse? {
        val result = safeAPiCall { lgRestApiService.getTvTickerMessages(ua) }
        return ApiResponseParsing().getResponseAsObject(result.data, TickerResponse::class)
    }

}