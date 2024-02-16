package com.diipl.moviebeam.data.dto.movies

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    var imagePathPoster: String = "",
    var imagePathSushi: String = "",
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

class Converters{
    @TypeConverter
    fun fromMap(value: Map<String, String>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String?): Map<String, String>? {
        val mapType = object : TypeToken<Map<String, String>?>() {}.type
        return Gson().fromJson(value, mapType)
    }
}