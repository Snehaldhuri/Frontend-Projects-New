package com.diipl.moviebeam.data.dto.localattraction

import com.diipl.moviebeam.data.dto.hotelservice.ImageDTO
import com.diipl.moviebeam.data.dto.hotelservice.LanguageWiseServiceDTO
import kotlinx.serialization.Serializable

@Serializable
data class LAServices(
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
    val serviceList: List<LAService> = emptyList(),
    val spotlightImage: ImageDTO = ImageDTO(),
    val videoAvailable: Boolean = false
)