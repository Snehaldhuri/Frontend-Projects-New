package com.diipl.moviebeam.data.dto.hotelservice

data class Services(
    val active: Boolean,
    val categoryId: Int,
    val categoryName: String,
    val contentType: String,
    val contentTypeId: Int,
    val defaultImage: ImageDTO,
    val editFlag: Boolean,
    val id: Int,
    val isSpecial: Boolean,
    val languageWiseServiceList: Map<String, LanguageWiseServiceDTO>,
    val serviceList: List<Service>,
    val spotlightImage: ImageDTO,
    val videoAvailable: Boolean
)