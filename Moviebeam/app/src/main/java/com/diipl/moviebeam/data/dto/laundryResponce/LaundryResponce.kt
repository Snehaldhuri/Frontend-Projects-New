package com.diipl.moviebeam.data.dto.laundryResponce


import com.google.gson.annotations.SerializedName

data class LaundryResponce(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("laundryDataList")
    var laundryDataList: List<LaundryDataList>,
    @SerializedName("type")
    var type: String = "",
    @SerializedName("version")
    var version: String = ""
)

data class LaundryDataList(
    @SerializedName("categoryName")
    var categoryName: String = "",
    @SerializedName("langWiseList")
    var langWiseList: LangWiseList = LangWiseList(),
    @SerializedName("laundryTypeId")
    var laundryTypeId: Int = 0,
    @SerializedName("subCategoryList")
    var subCategoryList: List<SubCategoryList>
) {
    data class LangWiseList(
        @SerializedName("en-US")
        var enUS: EnUS = EnUS()
    ) {
        data class EnUS(
            @SerializedName("categoryName")
            var categoryName: String = ""
        )
    }

}


data class SubCategoryList(
    @SerializedName("categoryName")
    var categoryName: String = "",
    @SerializedName("dispPrice")
    var dispPrice: String = "",
    @SerializedName("langWiseList")
    var langWiseList: LangWiseList = LangWiseList(),
    @SerializedName("laundryItemId")
    var laundryItemId: Int = 0,
    @SerializedName("laundryItemTypeId")
    var laundryItemTypeId: Int = 0,
    @SerializedName("price")
    var price: Double = 0.0,
    @SerializedName("subTitle")
    var subTitle: String = "",
    @SerializedName("title")
    var title: String = ""
) {
    data class LangWiseList(
        @SerializedName("en-US")
        var enUS: EnUS = EnUS()
    ) {
        data class EnUS(
            @SerializedName("subTitle")
            var subTitle: String = "",
            @SerializedName("title")
            var title: String = ""
        )
    }
}
