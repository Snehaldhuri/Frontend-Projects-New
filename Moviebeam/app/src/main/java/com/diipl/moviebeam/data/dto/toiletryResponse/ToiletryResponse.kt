package com.diipl.moviebeam.data.dto.toiletryResponse


import com.google.gson.annotations.SerializedName

data class ToiletryResponse(
    @SerializedName("ToiletryDataList")
    var toiletryDataList: List<ToiletryData> = listOf()
) {
    data class ToiletryData(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("imgSrc")
        var imgSrc: String = "",
        @SerializedName("langWiseList")
        var langWiseList: LangWiseList = LangWiseList(),
        @SerializedName("name")
        var name: String = "",
        var quantity: Int = 0
    ) {
        data class LangWiseList(
            @SerializedName("en-US")
            var enUS: EnUS = EnUS(),
            @SerializedName("es-MX")
            var esMX: EsMX = EsMX()
        ) {
            data class EnUS(
                @SerializedName("name")
                var name: String = ""
            )

            data class EsMX(
                @SerializedName("name")
                var name: String = ""
            )
        }
    }
}