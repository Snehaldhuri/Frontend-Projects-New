package com.diipl.moviebeam.data.dto.flightstatus

data class FlightStatusResponse(
    val flightList: List<Flight>,
    val id: Int,
    val type: String
)