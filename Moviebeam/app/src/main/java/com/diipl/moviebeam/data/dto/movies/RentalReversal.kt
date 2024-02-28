package com.diipl.moviebeam.data.dto.movies

import com.diipl.moviebeam.utils.Constants
import com.google.gson.annotations.SerializedName

data class RentalReversalResponse(
    @SerializedName("description")
    var description: String = "",
    @SerializedName("errorCode")
    var errorCode: Int = 0,
    @SerializedName("type")
    var type: String = ""
)

data class RentalReversalRequest(
    @SerializedName("Q")
    var type: String = "RENTAL_REVERSAL",
    @SerializedName("UA")
    var UA: String = Constants.UA,
    @SerializedName("REVERSAL_DETAILS")
    var reversalDetails: String = ""
)