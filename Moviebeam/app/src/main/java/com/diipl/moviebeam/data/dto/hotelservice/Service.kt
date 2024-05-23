package com.diipl.moviebeam.data.dto.hotelservice

import kotlinx.serialization.Serializable


@Serializable
data class Service(
    val active: Boolean = false,
    val categoryId: Int = 0,
    val categoryName: String = "",
    val contentType: String = "",
    val contentTypeId: Int = 0,
    val defaultImage: ImageDTO = ImageDTO(),
    val description: String = "",
    val editFlag: Boolean = false,
    val id: Int = 0,
    val imagePathPoster: String = "",
    val imagePathSushi: String = "",
    val isSpecial: Boolean = false,
    val landscapeRatio: Int = 0,
    val languageWiseServiceList: Map<String, LanguageWiseServiceDTO> = emptyMap(),
    val layout: Int = 0,
    val secServiceImageList: List<String> = emptyList(),
    val serviceId: Int = 0,
    val serviceImageList: List<String> = emptyList(),
    val serviceImageListNew: List<String> = emptyList(),
    val spotlightImage: ImageDTO = ImageDTO(),
    val title: String = "",
    val versionNo: Int = 0,
    val videoAvailable: Boolean = false,

//    val Integer serviceDetailId;
//val Integer categoryDetailId;
//
//val Integer languageId;
//val String languageName;
//val Integer accountToLanguageId;
//val Integer isDefaultlanguage;
//val Integer serviceId;
//val Integer categoryId;
//
//val List<LgServiceDTO> languageWiseDetailsTabList;
//
//val List<LgServiceDTO> languageWiseDetailsTabForCategoryList;
//
//val LinkedHashMap<String, LanguageWiseServiceDTO> languageWiseServiceList;
//
//val String categoryName;
//val Integer contentTypeId;
//val String contentType;
//
//val String title;
//val String description;
//val Integer versionNo;
//val String imagePathSushi;
//val String imagePathPoster;
//
//val String secImagePathSushi;
//val String secImagePathPoster;
//
//val String videoPath;
//
//// for Cloud Image URl
//val String imagePathSushiCloud;
//val String imagePathPosterCloud;
//val ArrayList<String> secServiceImageListCloud;
//val ArrayList<String> serviceImageListCloud;
//val String imagePathPosterNewCloud;
//val ArrayList<String> serviceImageListNewCloud;
//
//// for UI SCREEN
//val String accountName;
//val Integer accountId;
//val Integer accountStatus;
//val Date lastUpdated;
//val Integer lastUpdatedBy;
//val boolean editFlag;
//val String imageNamePoster;
//
//val Integer oldVersionNo;
//
//val boolean videoAvailable;
//val Integer noOfFiles;
//
//val String releaseVersionNo;
//val String contentJson;
//val Date releaseDate;
//val Integer releaseBy;
//val Integer categorySortOrder;
//val Integer sortOrder;
//val Integer layout;
//val Integer landscapeRatio;
//
//val boolean isActive;
//
//val Integer cropSelection;
//
//
//// for NEW UI Variables
//val String newUiImageNamePoster;
//val Integer newUiOldVersionNo;
//val Integer newUiCropSelection;
//val Integer guestServiceImageId;
//val String imagePathPosterNew;
//val ArrayList<String> serviceImageListNew;
)