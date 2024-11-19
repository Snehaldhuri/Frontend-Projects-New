package com.diipl.moviebeam.data.repositories

import android.util.Log
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
import com.diipl.moviebeam.data.remote.datasource.RemoteDataSource
import javax.inject.Inject

class MovieBeamRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getMovieAccess(request: RentalMovieRequest): RentalMovieResponse? {
        return remoteDataSource.getMoviesAccess(request)
    }

    suspend fun getWeatherData(ua: String): WeatherResponse? {
        return remoteDataSource.getWeatherData(ua)
    }

    suspend fun getHotelServiceInfo(accountId: String): HotelServiceResponse? {
        return remoteDataSource.getHotelServiceInfo(accountId)
    }

    suspend fun getThemeDetails(ua: String): ThemeResponse? {
        return remoteDataSource.getThemeDetails(ua)
    }

    suspend fun getDateTimeData(ua: String): DateTimeResponse? {
        return remoteDataSource.getDateTimeData(ua)
    }

    suspend fun getAccountSetupDetails(
        cmd: String,
        ua: String,
        mode: String
    ): AccountSetupResponse? {
        return remoteDataSource.getAccountSetupDetails(cmd, ua, mode)
    }

    suspend fun getFlightStatus(
        cmd: String,
        ua: String,
        callType: String,
        apCode: String,
        mode: String
    ): FlightStatusResponse? {
        return remoteDataSource.getFlightStatus(cmd, ua, callType, apCode, mode)
    }

    suspend fun getLocalAttractionInfo(ua: String): LocalAttractionResponse? {
        return remoteDataSource.getLocalAttractionInfo(ua)
    }

    suspend fun getNewsHeader(ua: String, languageId: Int): NewsHeaderResponse? {
        return remoteDataSource.getNewsHeader(ua, languageId)
    }

    suspend fun getMoviesInfo(ua: String): MoviesResponse? {
        return remoteDataSource.getMoviesInfo(ua)
    }

    suspend fun getShowtimeInfo(ua: String): ShowTimeResponse? {
        return remoteDataSource.getShowtimeInfo(ua)
    }

    suspend fun getNewsDetails(newsId: Int): NewsResponse? {
        return remoteDataSource.getNewsDetails(newsId)
    }

    suspend fun getNewsDetails(ua: String, newsId: Int): NewsResponse? {
        return remoteDataSource.getNewsDetails(ua,newsId)
    }

    suspend fun processStbMaster(
        ua: String,
        srNo: String,
        macAddress: String,
        wifiMacAddress: String,
        stbType: String
    ): StbMasterResponse? {
        return remoteDataSource.processStbMaster(ua, srNo, macAddress, wifiMacAddress, stbType)
    }

    suspend fun getLaundryDetails(ua: String, srno: String): LaundryResponce? {
        return remoteDataSource.laundryResponse(ua, srno)
    }

    suspend fun sendGuestFeedback(
        ua: String,
        feedback: String,
        stbTime: String
    ): FeedbackResponse? {
        return remoteDataSource.sendGuestFeedback(ua, feedback, stbTime)
    }

    suspend fun setRentalReversal(request: RentalReversalRequest): RentalReversalResponse? {
        return remoteDataSource.setRentalReversal(request)
    }

    suspend fun buyPassRequest(request: AdultDayPassRequest): DayPassResponse? {
        return remoteDataSource.buyPassRequest(request)
    }

    suspend fun getChannelList(ua: String): ChannelListResponse? {
        return remoteDataSource.getChannelList(ua)
    }

    suspend fun getEPGFromCloud(url: String): EPGResponse? {
        return remoteDataSource.getEPGFromCloud(url)
    }

    suspend fun getEPGDataFromServer(ua: String): EPGResponse? {
        return remoteDataSource.getEPGDataFromServer(ua)
    }

    suspend fun sendSysInfo(ua: String, body: SysInfoDTO): Int? {
        return remoteDataSource.sendSysInfo(ua, body)
    }

    suspend fun getSoftwareUpdateDetails(stbTypeId: Int, ua: String): SoftwareUpgradeResponse? {
        return remoteDataSource.getSoftwareUpdateDetails(stbTypeId, ua)
    }

    suspend fun getTvTickerMessages(ua: String): TickerResponse? {
        return remoteDataSource.getTvTickerMessages(ua)
    }

    suspend fun getGuestMessages(ua: String, guestSessionId: String): MessageResponse? {
        return remoteDataSource.getGuestMessages(ua, guestSessionId)
    }

    suspend fun getNewMovieAccess(
        q: String,
        UA: String,
        RID: Int,
        PID: Int,
        price: Double,
        timeStamp: Long,
        seek: Long,
        sessionID: String,
        a: Int,
        ra: Int,
        cType: String,
        seekType: Long,
        rentalID: String,
        contentTypeID: Int,
        productType: Int,
        vodMID: Long,
        AID: Long,
        mode: String
    ): RentalMovieResponse? {
        Log.d("TAG", "getVodData: ${remoteDataSource.getNewMoviesAccess(
            q, UA, RID, PID, price, timeStamp, seek, sessionID, a, ra, cType, seekType,
            rentalID, contentTypeID, productType, vodMID, AID, mode
        )}")
        return remoteDataSource.getNewMoviesAccess(
            q, UA, RID, PID, price, timeStamp, seek, sessionID, a, ra, cType, seekType,
            rentalID, contentTypeID, productType, vodMID, AID, mode
        )
    }

    suspend fun getVodData(
//        vodMgrIp: String,
//        vodMgrPort: String,
        vodMid: Int ,
        transId: Long,
        streamingType: String,
        mode: String ,
        ua: String,
        rentalId: Int,
        productId: Int,
        priority: Int ,
        fileName: String,
        contentType: String,
        seek: Long,
        json: Boolean
    ): String? {
        val result = remoteDataSource.getVodData(
            vodMid,transId,streamingType,mode,ua,rentalId,productId,priority,fileName,contentType,seek,json
        )
        Log.d("TAG", "getVodData: ${remoteDataSource.getVodData(
            vodMid,transId,streamingType,mode,ua,rentalId,productId,priority,fileName,contentType,seek,json
        )}")
        return result
    }
}