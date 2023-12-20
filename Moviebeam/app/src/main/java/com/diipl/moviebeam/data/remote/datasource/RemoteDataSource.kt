package com.diipl.moviebeam.data.remote.datasource

import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.hotelservice.HotelServiceResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.remote.services.AccountSetupApiService
import com.diipl.moviebeam.data.remote.services.LgRestApiService
import com.diipl.moviebeam.utils.ApiResponseParsing
import com.diipl.moviebeam.utils.NetworkHandler
import com.diipl.moviebeam.utils.NetworkUtils
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val lgRestApiService: LgRestApiService,
    private val accountSetupApiService: AccountSetupApiService
) : NetworkHandler(networkUtils){

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

    suspend fun getAccountSetupDetails(cmd: String, ua: String, mode: String): AccountSetupResponse? {
        val result = safeAPiCall {
            accountSetupApiService.getAccountSetupDetails(cmd, ua, mode)
        }
        return ApiResponseParsing().getResponseAsObject(result.data,AccountSetupResponse::class)
    }

}