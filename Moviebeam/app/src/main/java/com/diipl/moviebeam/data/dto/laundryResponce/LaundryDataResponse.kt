package com.diipl.moviebeam.data.dto.laundryResponce

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LaundryDataResponse(
    val laundryDataList: List<LaundryCategory>
)

@Serializable
data class LaundryCategory(
    val categoryName: String,
    val id: Int,
    val langWiseList: Map<String, LangWiseCategory>,
    val subCategoryList: List<LaundrySubCategory>
)

@Serializable
data class LangWiseCategory(
    @SerialName("en-US") val enUS: LangWiseItem,
    @SerialName("es-MX") val esMX: LangWiseItem
)

@Serializable
data class LangWiseItem(
    val categoryName: String
)

@Serializable
data class LaundrySubCategory(
    val title: String,
    val dispPrice: String,
    val price: Double,
    val id: Int,
    val imgSrc: String,
    val categoryName: String,
    var quantity: Int = 0,
    val subTitle: String,
    val langWiseList: Map<String, LangWiseSubCategory>
)

@Serializable
data class LangWiseSubCategory(
    @SerialName("en-US") val enUS: LangWiseItem,
    @SerialName("es-MX") val esMX: LangWiseItem
)
