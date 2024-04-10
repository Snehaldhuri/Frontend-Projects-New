package com.diipl.moviebeam.data.dto.ticker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TvTickerLanguageDTO(
    val message: String?
) : Parcelable