package com.diipl.moviebeam.data.dto.movies

import kotlinx.serialization.Serializable

@Serializable
data class ContentDto(
    val actor: String = "",
    val contentType: String = "",
    val contentTypeId: Int = 0,
    val director: String = "",
    val episodesPresent: Boolean  =false,
    val genre1: String = "",
    val genre1Id: Int = 0,
    val genre2: String = "",
    val genre2Id: Int = 0,
    val headingDetails: String = "",
    val headingDetailsForLanguage: String = "",
    val headingDetailsNew: String = "",
    val id: Int = 0,
    val imagePathPoster: String = "",
    val imagePathSushi: String = "",
    val imgTap: String = "",
    val languageSynopsisList: Map<String, String> = emptyMap(),
    val movieName: String = "",
    val ppv: Int = 0,
    val price: Double = 0.0,
    val productCategory: String = "",
    val productCategoryId: Int = 0,
    val productClass: String = "",
    val productId: Int = 0,
    val qos: String = "",
    val releaseId: Int = 0,
    val releaseTypeId: Int = 0,
    val rentalDuration: Int = 0,
    val secImagePathPoster: String = "",
    val secImagePathSushi: String = "",
    val sushiHoverText: String = "",
    val synopsis: String = "",
    val trailerAvailable: Boolean = false,
    val trailerDuration: String = "",
    val trailerVideoPath: String = "",
    val videoPath: String = ""
)