package com.diipl.moviebeam.data.dto.kaping

import com.google.gson.annotations.SerializedName

data class KapingResponse(
    @SerializedName("AS") var AS: String? = null,
    @SerializedName("CALLBACK") var CALLBACK: Int? = null,
    @SerializedName("CMD") var CMD: String? = null,
    @SerializedName("UA") var UA: String? = null,
    @SerializedName("type") var type: String? = null
)