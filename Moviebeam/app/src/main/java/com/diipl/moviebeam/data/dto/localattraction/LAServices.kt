package com.diipl.moviebeam.data.dto.localattraction

import com.diipl.moviebeam.data.dto.hotelservice.ImageDTO
import com.diipl.moviebeam.data.dto.hotelservice.LanguageWiseServiceDTO

data class LAServices(
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
    val serviceList: List<LAService>,
    val spotlightImage: ImageDTO,
    val videoAvailable: Boolean
)