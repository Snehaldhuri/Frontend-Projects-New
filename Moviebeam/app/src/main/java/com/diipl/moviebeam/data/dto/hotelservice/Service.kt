package com.diipl.moviebeam.data.dto.hotelservice

import kotlinx.serialization.Serializable


@Serializable
data class Service(
    val active: Boolean = false,
    val categoryId: Int = 0,
    val categoryName: String = "",
    val contentType: String = "",
    val contentTypeId: Int = 0,
    val defaultImage: ImageDTO = ImageDTO(),
    val description: String = "",
    val editFlag: Boolean = false,
    val id: Int = 0,
    val imagePathPoster: String = "",
    val imagePathSushi: String = "",
    val isSpecial: Boolean = false,
    val landscapeRatio: Int = 0,
    val languageWiseServiceList: Map<String, LanguageWiseServiceDTO> = emptyMap(),
    val layout: Int = 0,
    val secServiceImageList: List<String> = emptyList(),
    val serviceId: Int = 0,
    val serviceImageListCloud: List<String> = emptyList(),
    val serviceImageListNewCloud: List<String> = emptyList(),
    val spotlightImage: ImageDTO = ImageDTO(),
    val title: String = "",
    val versionNo: Int = 0,
    val videoAvailable: Boolean = false
)