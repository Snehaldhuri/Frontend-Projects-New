package com.diipl.moviebeam.data.dto.hotelservice

import kotlinx.serialization.Serializable

@Serializable
data class HotelServiceResponse(
    val id: Int = 0,
    val servicesList: List<Services> = emptyList(),
    val type: String = "",
    val version: String = ""
)