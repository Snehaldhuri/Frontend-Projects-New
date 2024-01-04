package com.diipl.moviebeam.data.dto.localattraction

import kotlinx.serialization.Serializable

@Serializable
data class LocalAttractionResponse(
    val id: Int = 0,
    val servicesList: List<LAServices>? = emptyList(),
    val type: String = "",
    val version: String = ""
)