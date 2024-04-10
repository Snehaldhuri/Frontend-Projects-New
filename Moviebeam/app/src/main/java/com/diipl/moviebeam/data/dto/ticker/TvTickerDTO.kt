package com.diipl.moviebeam.data.dto.ticker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TvTickerDTO(
    val all: Int?,
    val assignedRooms: List<String>?,
    val displayScreen: Int?,
    val et: Long,
    val etStr: String?,
    val langWiseList: Map<String, TvTickerLanguageDTO>?,
    val msg: String?,
    val st: Long,
    val stStr: String?
) : Parcelable