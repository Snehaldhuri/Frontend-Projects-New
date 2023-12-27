package com.diipl.moviebeam.data.dto.accountsetup

import kotlinx.serialization.Serializable

@Serializable
data class LanguageWiseHotelCustomizationDTO(
    val hotelDisplayName: String = "",
    val hotelInfo: String = ""
)