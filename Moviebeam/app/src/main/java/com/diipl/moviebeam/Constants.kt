package com.diipl.moviebeam

import com.diipl.moviebeam.data.dto.btn.BtnModel

object Constants {
    const val SPLASH_DELAY = 3000
    const val BASE_URL = "https://stb.moviebeam.com:1930/LG/rest/"
    const val BASE_URL_LG_REST = "https://stb.moviebeam.com:1930/LG/rest/"
    const val BASE_URL_ACCOUNT_SETUP = "https://stb.moviebeam.com:1926/"
    const val API_TIME_OUT_IN_SEC = 60L
    const val INTERNET_ERROR_MESSAGE = "Internet Connection Not Available"
    const val SERVER_ERROR = "Server Error"
    const val LG_REST = "LG_REST"
    const val ACCOUNT_SETUP = "ACCOUNT_SETUP"
    const val HELP_INFO = "Help & Info"
    const val SYSTEM_INFO = "System Info"
    const val HOTEL_INFORMATION = "Hotel Information"
    const val DEFAULTGRADIENTSTARTCOLOR = "#85bf08"
    const val DEFAULTGRADIENTENDCOLOR = "#0ca654"


    //Home Page Menu Button Id
    const val PRG_GUIDE_ID = "prgGuide"
    const val VOD_ID = "vod"
    const val SHOWTIMES_ID = "showtimes"
    const val CASTING_ID = "casting"
    const val APPS_ID = "apps"
    const val GUEST_SERVICES_ID = "guestServices"
    const val HOTEL_SERVICES_ID = "hotelServices"
    const val CRACKLE_DEFAULT_ID = "crackleDefault"
    const val IN_ROOM_DINING_ID = "inRoomDining"
    const val LOCAL_ATTRACTION_ID = "lam"
    const val FOOD_DELIVERY_ID = "foodDelivery"

    //Home Page Menu Button Title
    const val PROGRAM_GUIDE = "Program Guide"
    const val MOVIES_MORE = "Movies & More"
    const val SHOWTIME = "Showtime"
    const val CASTING = "Casting"
    const val APPS = "Apps"
    const val GUEST_SERVICES = "Guest Services"
    const val HOTEL_SERVICES = "Hotel Info"
    const val CRACKLE_DEFAULT = "Crackle"
    const val IN_ROOM_DINING = "In Room Dining"
    const val LOCAL_ATTRACTION = "Local Attraction"
    const val FOOD_DELIVERY = "Food Delivery"


    //Movies page menu button Id
    const val MOVIE_RENTALS_ID = "movieRentals"
    const val FREE_MOVIES_ID = "freeMovies"
    const val ADULT_DAY_PASS_ID = "adultDayPass"
    const val ADULT_ID = "adult"

    //Movies page menu button list
    const val MOVIE_RENTALS = "Movie Rentals"
    const val FREE_VOD = "Free Movies"
    const val ADULT_DAY_PASS = "Adult Day Pass"
    const val ADULT = "Adult"

    val HOME_PAGE_MENU_BUTTON_LIST = listOf(
        BtnModel(PRG_GUIDE_ID, R.drawable.program_guide_icon, PROGRAM_GUIDE),
        BtnModel(VOD_ID, R.drawable.video_on_demand_icon, MOVIES_MORE),
        BtnModel(SHOWTIMES_ID, R.drawable.showtime_icon, SHOWTIME),
        BtnModel(CASTING_ID, R.drawable.casting_icon, CASTING),
        BtnModel(APPS_ID, R.drawable.app_world_icon, APPS),
        BtnModel(GUEST_SERVICES_ID, R.drawable.guestservices_icon, GUEST_SERVICES),
        BtnModel(HOTEL_SERVICES_ID, R.drawable.hotelservices_icon, HOTEL_SERVICES),
        BtnModel(CRACKLE_DEFAULT_ID, R.drawable.crackle_white_icon, CRACKLE_DEFAULT),
        BtnModel(IN_ROOM_DINING_ID, R.drawable.crackle_white_icon, IN_ROOM_DINING),
        BtnModel(LOCAL_ATTRACTION_ID, R.drawable.localattraction_icon, LOCAL_ATTRACTION),
        BtnModel(FOOD_DELIVERY_ID, R.drawable.fooddelivery_icon, FOOD_DELIVERY)
    )

    const val MOVIE_URL = "http://d3rh9vbn3pp0qe.cloudfront.net/41177_T.m2t"


    const val MOVIE_URL1 = "https://d3rh9vbn3pp0qe.cloudfront.net/41177_T.ts"
    const val MOVIE_URL2 = "https://d3rh9vbn3pp0qe.cloudfront.net/41132_T.ts"
    const val MOVIE_URL3 = "https://d3rh9vbn3pp0qe.cloudfront.net/41191_T.ts"
    const val TRAILER_URL = "trailer_url"


    val MOVIES_PAGE_MENU_BUTTON_LIST = listOf(
        BtnModel(MOVIE_RENTALS_ID, R.drawable.movie_rentals_img, MOVIE_RENTALS),
        BtnModel(FREE_MOVIES_ID, R.drawable.video_on_demand_icon, FREE_VOD),
        BtnModel(ADULT_DAY_PASS_ID, R.drawable.adult_day_pass, ADULT_DAY_PASS),
        BtnModel(ADULT_ID, R.drawable.adult, ADULT),
    )
}

const val SPLASH_DELAY = 3000
