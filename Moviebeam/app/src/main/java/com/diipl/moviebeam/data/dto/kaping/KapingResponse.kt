package com.diipl.moviebeam.data.dto.kaping

import android.os.Parcelable
import com.diipl.moviebeam.data.kaping.CmdDto
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KapingResponse(
    @SerializedName("AS") var AS: String? = null,
    @SerializedName("CALLBACK") var CALLBACK: Int? = null,
    @SerializedName("CMD") var CMD: String? = null,
    @SerializedName("UA") var UA: String? = null,
    @SerializedName("type") var type: String? = null,
    var cmdData: CmdDto? = null
) : Parcelable