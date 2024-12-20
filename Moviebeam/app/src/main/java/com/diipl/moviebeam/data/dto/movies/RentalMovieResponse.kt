package com.diipl.moviebeam.data.dto.movies

import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RentalMovieResponse(
    @SerializedName("A") var a: Int = 0,
    @SerializedName("BALANCE") var balance: Int = 0,
    @SerializedName("ECM") var ECM: String = "",
    @SerializedName("ENCRYPTION_TYPE") var encryptionType: Int = 0,
    @SerializedName("ERROR_CODE") var errorCode: Int = 0,
    @SerializedName("type") var type: String = "",
    @SerializedName("RENTALID") var rentalID: String = "",
    @SerializedName("UA") var UA: String = ""
)


@Serializable
data class VodMovieResponse(
    @SerializedName("ERROR_CODE") var errorCode :Int = 0,
    @SerializedName("DESCRIPTION") var description: String = "",
    @SerializedName("LCN") var lcn: String = "",
    @SerializedName("TRANSID") var transId: Long = 0,
    @SerializedName("CHANNELTYPE") var channelType: String = "",
    @SerializedName("CHANNELBROADCASTTYPE") var channelBroadcastType: String = "",
    @SerializedName("IP") var ip: String = "",
    @SerializedName("PORT") var port: String = "",
    @SerializedName("MAJOR") var major: String = "",
    @SerializedName("MINOR") var minor: String = ""
)


data class RentalSyncResponse(
    @SerializedName("syncList")
    var syncList: List<Sync> = listOf()
) {
    data class Sync(
        @SerializedName("productId")
        var productId: Int = 0,
        @SerializedName("releaseId")
        var releaseId: Int = 0,
        @SerializedName("releaseTypeId")
        var releaseTypeId: Int = 0,
        @SerializedName("rentalId")
        var rentalId: Int = 0,
        @SerializedName("rentalTime")
        var rentalTime: String = "",
        @SerializedName("seek")
        var seek: Long = 0
    )
}

data class AdultDayPassRequest(
    @SerializedName("Q")
    var Q: String = "PURCHASE_PASS",
    @SerializedName("UA")
    var UA: String = "",
    @SerializedName("TYPE")
    var type: String = "D1001",
    @SerializedName("PRICE_LIST")
    var priceList: String = "1",
    @SerializedName("PRICE")
    var price: Int = 0,
    @SerializedName("TIMESTAMP")
    var timeStamp: Long = System.currentTimeMillis()/1000,
    @SerializedName("SID")
    var sessionID: String = GuestDetails.SESSION_ID,
    @SerializedName("MODE")
    var mode: String = "JSON"
)

data class DayPassResponse(
    @SerializedName("UA")
    var UA: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("ERROR_CODE")
    var errorCode: Int = 0
)


data class AdultDayPassSync(
    @SerializedName("dayPassList")
    var dayPassList: List<DayPass> = listOf()
) {
    data class DayPass(
        @SerializedName("dayPassRentalTime")
        var dayPassRentalTime: String = "",
        @SerializedName("daypassId")
        var daypassId: Int = 0
    )
}

@Serializable
data class RentalMovieRequest(
    @SerializedName("Q") var q: String = "RENTAL",
    @SerializedName("UA") var UA: String = "",
    @SerializedName("RID") var releaseID: Int = 0,
    @SerializedName("PID") var productId: Int = 0,
    @SerializedName("PRICE") var price: Double = 00.00,
    @SerializedName("TIMESTAMP") var timeStamp: Long = System.currentTimeMillis() / 1000,
    @SerializedName("SEEK") var seek: Long = 0,
    @SerializedName("SID") var sessionID: String = GuestDetails.SESSION_ID,
    @SerializedName("A") var a: Int = 1,
    @SerializedName("RA") var ra: Int = 1,
    @SerializedName("CTYPE") var cType: String = Constants.C_TYPE_MOVIE,
    @SerializedName("SEEKTYPE") var seekType: Long = 0,
    @SerializedName("RENTALID") var rentalID: String = "",
    @SerializedName("CONTENT_TYPE_ID") var contentTypeID: Int = 0,
    @SerializedName("PRODUCT_TYPE") var productType: Int = 0,
    @SerializedName("VODMID") var vodMID: Long = 1,
    @SerializedName("AID") var AID: Long = 0,
    @SerializedName("MODE") var mode: String = "JSON"
)