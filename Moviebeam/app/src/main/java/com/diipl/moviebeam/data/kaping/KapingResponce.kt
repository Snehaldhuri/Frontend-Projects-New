package com.diipl.moviebeam.data.kaping

import com.google.gson.annotations.SerializedName

data class kapingResponce(
    @SerializedName("AS") var AS: String? = null,
    @SerializedName("CALLBACK") var CALLBACK: Int? = null,
    @SerializedName("CMD") var CMD: String? = null,
    @SerializedName("UA") var UA: String? = null,
    @SerializedName("type") var type: String? = null
)
