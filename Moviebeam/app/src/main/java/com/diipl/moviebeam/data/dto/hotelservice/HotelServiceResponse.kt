package com.diipl.moviebeam.data.dto.hotelservice

data class HotelServiceResponse(
    val id: Int,
    val servicesList: List<Services>,
    val type: String,
    val version: String
)