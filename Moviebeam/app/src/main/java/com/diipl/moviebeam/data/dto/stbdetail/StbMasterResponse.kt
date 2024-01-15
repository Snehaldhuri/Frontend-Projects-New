package com.diipl.moviebeam.data.dto.stbdetail

import com.google.gson.annotations.SerializedName

data class StbMasterResponse(
    @SerializedName("type"      ) var type      : String? = null,
    @SerializedName("errorCode" ) var errorCode : Int?    = null,
    @SerializedName("desc"      ) var desc      : String? = null,
    @SerializedName("id"        ) var id        : Int?    = null
)
