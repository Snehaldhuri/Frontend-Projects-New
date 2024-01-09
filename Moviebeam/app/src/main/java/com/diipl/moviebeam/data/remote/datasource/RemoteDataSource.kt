package com.diipl.moviebeam.data.remote.datasource

import android.util.Log
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.flightstatus.FlightStatusResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.news.NewsHeaderResponse
import com.diipl.moviebeam.data.dto.news.NewsResponse
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.kapingResponce
import com.diipl.moviebeam.data.remote.services.AccountSetupApiService
import com.diipl.moviebeam.data.remote.services.AssetApiService
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.utils.ApiResponseParsing
import com.diipl.moviebeam.utils.NetworkHandler
import com.diipl.moviebeam.utils.NetworkUtils
import retrofit2.http.Query
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val lgRestApiService: LgRestApiService,
    private val accountSetupApiService: AccountSetupApiService,
    private val assetApiService: AssetApiService
) : NetworkHandler(networkUtils) {

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        val result = safeAPiCall {
            lgRestApiService.getWeather(ua)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, WeatherResponse::class)
    }

    suspend fun getHotelServiceInfo(accountId: Int): HotelServiceResponse? {
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
        cmd: String,
        ua: String,
        mode: String
    ): AccountSetupResponse? {
        val result = safeAPiCall {
            accountSetupApiService.getAccountSetupDetails(cmd, ua, mode)
        }
        return ApiResponseParsing().getResponseAsObject(result.data, AccountSetupResponse::class)
    }

    suspend fun getFlightStatus(
        cmd: String,
        ua: String,
        callType: String,
        apCode: String,
        mode: String
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


    suspend fun getStbMasterDetails(
        ua: String,
        srno: String,
        macadd: String,
        type: String,
        wifimacadd: String
    ): StbMasterResponse? {
        val result = safeAPiCall {
            lgRestApiService.getstbMaster(
                ua,
                srno,
                macadd,
                type,
                wifimacadd
            )
        }
        Log.e("result", "getStbMasterDetails:${result} ")
        return ApiResponseParsing().getResponseAsObject(result.data, StbMasterResponse::class)
    }


    suspend fun kapingResponce(
        Q: String,
        UA: String,
        DRID: String,
        SW: String,
        CLISTVER: String,
        DV: String,
        TNS: String,
        EVENT: String,
        SID: String,
        RBTY: String,
        MODE: String,
        LAVER: String,
        HSVER: String,
        THMVER: String,
        CMDRES: String,
        CALLBACKFLG: String,
        INRMVER: String,
        LAUVER: String
    ): kapingResponce {
        val result = safeAPiCall {
            lgRestApiService.kaping(
                Q,
                UA,
                DRID,
                SW,
                CLISTVER,
                DV,
                TNS,
                EVENT,
                SID,
                RBTY,
                MODE,
                LAVER,
                HSVER,
                THMVER,
                CMDRES,
                CALLBACKFLG,
                INRMVER,
                LAUVER

            )
        }
        Log.e("result", "getStbMasterDetails:${result} ")
        return ApiResponseParsing().getResponseAsObject(result.data, kapingResponce::class)!!
    }
}