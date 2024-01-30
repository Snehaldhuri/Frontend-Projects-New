package com.diipl.moviebeam.data.dto.laundryResponce


data class LaundryResponce(
    val laundryDataList: List<LaundryDataList>,
    val id: Long,
    val type: String,
    val version: String,
)
data class LaundryDataList(
    val laundryTypeId: Long,
    val langWiseList: LangWiseList,
    val subCategoryList: List<SubCategoryList>,
    val categoryName: String,
)

data class LangWiseList(
    @JsonProperty("en-US")
    val enUs: EnUs,
)

annotation class JsonProperty(val value: String)

data class EnUs(
    val categoryName: String,
)

data class SubCategoryList(
    val subTitle: String,
    val price: Double,
    val langWiseList: LangWiseList2,
    val dispPrice: String,
    val laundryItemId: Long,
    val title: String,
    val categoryName: String,
    val laundryItemTypeId: Long,
)

data class LangWiseList2(
    @JsonProperty("en-US")
    val enUs: EnUs2,
)

data class EnUs2(
    val subTitle: String,
    val title: String,
)