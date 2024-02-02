package com.diipl.moviebeam.data.dto.kaping

import com.diipl.moviebeam.data.kaping.CmdDto
import com.google.gson.annotations.SerializedName

data class KapingResponse(
    @SerializedName("AS") var AS: String? = null,
    @SerializedName("CALLBACK") var CALLBACK: Int? = null,
    @SerializedName("CMD") var CMD: String? = null,
    @SerializedName("UA") var UA: String? = null,
    @SerializedName("type") var type: String? = null,
    var cmd2: CmdDto? = null
)