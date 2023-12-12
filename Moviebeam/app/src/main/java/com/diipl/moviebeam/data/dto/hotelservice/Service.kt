package com.diipl.moviebeam.data.dto.hotelservice

data class Service(
    val active: Boolean,
    val categoryId: Int,
    val categoryName: String,
    val contentType: String,
    val contentTypeId: Int,
    val defaultImage: ImageDTO,
    val description: String,
    val editFlag: Boolean,
    val id: Int,
    val imagePathPoster: String,
    val imagePathSushi: String,
    val isSpecial: Boolean,
    val landscapeRatio: Int,
    val languageWiseServiceList: Map<String, LanguageWiseServiceDTO>,
    val layout: Int,
    val secServiceImageList: List<String>,
    val serviceId: Int,
    val serviceImageList: List<String>,
    val serviceImageListNew: List<String>,
    val spotlightImage: ImageDTO,
    val title: String,
    val versionNo: Int,
    val videoAvailable: Boolean
)