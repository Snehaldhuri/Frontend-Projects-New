package com.diipl.moviebeam.data.dto.flightstatus

data class Flight(
    val airline: String?,
    val colorCode: String?,
    val departureTime: String?,
    val destination: String?,
    val flightNo: String?,
    val gate: String?,
    val id: Int?,
    val status: String?,
    val terminal: String?
)