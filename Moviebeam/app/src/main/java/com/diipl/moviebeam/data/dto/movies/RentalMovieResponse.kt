package com.diipl.moviebeam.data.dto.movies

import com.diipl.moviebeam.Constants
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
data class RentalMovieRequest(
    @SerializedName("Q") var q: String = "RENTAL",
    @SerializedName("UA") var UA: String = Constants.UA,
    @SerializedName("RID") var releaseID: Int = 0,
    @SerializedName("PID") var productId: Int = 0,
    @SerializedName("PRICE") var price: Double = 00.00,
    @SerializedName("TIMESTAMP") var timeStamp: Long = System.currentTimeMillis() / 1000,
    @SerializedName("SEEK") var seek: Long = 0,
    @SerializedName("SID") var sessionID: String = Constants.SESSION_ID,
    @SerializedName("A") var a: Int = 1,
    @SerializedName("RA") var ra: Int = 1,
    @SerializedName("CTYPE") var cType: String = Constants.C_TYPE_MOVIE,
    @SerializedName("SEEKTYPE") var seekType: Int = 0,
    @SerializedName("RENTALID") var rentalID: String = "",
    @SerializedName("CONTENT_TYPE_ID") var contentTypeID: Int = 0,
    @SerializedName("PRODUCT_TYPE") var productType: Int = 0,
    @SerializedName("VODMID") var vodMID: Long = 1,
    @SerializedName("AID") var AID: Long = 0,
    @SerializedName("MODE") var mode: String = "JSON"
)