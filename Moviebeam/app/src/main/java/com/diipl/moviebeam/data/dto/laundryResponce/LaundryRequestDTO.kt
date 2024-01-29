package com.diipl.moviebeam.data.dto.laundryResponce

data class LaundryRequestDTO(
    val categoryName: String,
    val langWiseList: Map<String, LaundryRequestDTO>,
    val laundryTypeId: Int,
    val subCategoryList: List<LaundryRequestDTO>,

    val dispPrice: String,
    val laundryItemId: Int,
    val laundryItemTypeId: Int,
    val price: Double,
    val subTitle: String,
    val title: String
)