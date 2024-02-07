package com.diipl.moviebeam.data.dto.laundryResponce

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
    val categoryName: String
)

@Serializable
data class LaundrySubCategory(
    val title: String,
    val dispPrice: String,
    val price: Double,
    val id: Int,
    val categoryName: String,
    val subTitle: String,
    val langWiseList: Map<String, LangWiseSubCategory>
)


@Serializable
data class LangWiseSubCategory(
    val title: String,
    val subTitle: String
)

