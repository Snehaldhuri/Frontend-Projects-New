package com.diipl.moviebeam.data.dto.laundryResponce

import com.google.gson.annotations.SerializedName

data class LaundryResponce(
    @SerializedName("laundryDataList") var laundryDataList: ArrayList<LaundryDataList> = arrayListOf(),
    @SerializedName("id") var id: Int? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("version") var version: String? = null
)
class EnUS(
    @SerializedName("categoryName")
    var categoryName: String? = null
)

class LangWiseList1(
    @SerializedName("en-US") var enUS: EnUS? = EnUS()
)

class EnUS2(
    @SerializedName("subTitle") var subTitle: String? = null,
    @SerializedName("title") var title: String? = null
)


class LangWiseList2(

    @SerializedName("en-US") var enUS: EnUS? = EnUS()

)

class SubCategoryList(

    @SerializedName("subTitle") var subTitle: String? = null,
    @SerializedName("price") var price: Double? = null,
    @SerializedName("langWiseList") var langWiseList1: LangWiseList1? = LangWiseList1(),
    @SerializedName("dispPrice") var dispPrice: String? = null,
    @SerializedName("laundryItemId") var laundryItemId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("categoryName") var categoryName: String? = null,
    @SerializedName("laundryItemTypeId") var laundryItemTypeId: Int? = null

)


class LaundryDataList(

    @SerializedName("laundryTypeId") var laundryTypeId: Int? = null,
    @SerializedName("langWiseList") var langWiseList2: LangWiseList2? = LangWiseList2(),
    @SerializedName("subCategoryList") var subCategoryList: ArrayList<SubCategoryList> = arrayListOf(),
    @SerializedName("categoryName") var categoryName: String? = null

)