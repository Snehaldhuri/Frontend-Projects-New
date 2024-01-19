package com.diipl.moviebeam.data.dto.hotelservice

import kotlinx.serialization.Serializable

@Serializable
data class Services(
    val active: Boolean = false,
    val categoryId: Int = 0,
    val categoryName: String = "",
    val contentType: String = "",
    val contentTypeId: Int = 0,
    val defaultImage: ImageDTO = ImageDTO(),
    val editFlag: Boolean = false,
    val id: Int = 0,
    val isSpecial: Boolean = false,
    val languageWiseServiceList: Map<String, LanguageWiseServiceDTO> = emptyMap(),
    val serviceList: List<Service> = emptyList(),
    val spotlightImage: ImageDTO = ImageDTO(),
    val videoAvailable: Boolean = false
)