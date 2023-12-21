package com.diipl.moviebeam.data.dto.localattraction

data class LocalAttractionResponse(
    val id: Int,
    val servicesList: List<LAServices>?,
    val type: String,
    val version: String
)