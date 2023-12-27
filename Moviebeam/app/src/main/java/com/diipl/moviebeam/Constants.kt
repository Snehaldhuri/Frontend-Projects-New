package com.diipl.moviebeam

import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.data.dto.btn.GsBtnModel

object Constants {
    const val SPLASH_DELAY = 3000
    const val BASE_URL = "https://stb.moviebeam.com:1930/LG/rest/"
    const val BASE_URL_LG_REST = "https://stb.moviebeam.com:1930/LG/rest/"
    const val BASE_URL_ACCOUNT_SETUP = "https://stb.moviebeam.com:1926/"
    const val BASE_URL_ASSET = "https://stb.moviebeam.com:1928/Asset/"
    const val API_TIME_OUT_IN_SEC = 60L
    const val INTERNET_ERROR_MESSAGE = "Internet Connection Not Available"
    const val SERVER_ERROR = "Server Error"
    const val LG_REST = "LG_REST"
    const val ACCOUNT_SETUP = "ACCOUNT_SETUP"
    const val ASSET = "ASSET"

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
    const val HELP_INFO = "Help & Info"

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
        BtnModel(LOCAL_ATTRACTION_ID, R.drawable.crackle_white_icon, LOCAL_ATTRACTION),
        BtnModel(FOOD_DELIVERY_ID, R.drawable.fooddelivery_icon, FOOD_DELIVERY)
    )

    //Guest Service Button Id
    const val WEATHER_ID = "weather"
    const val FLIGHT_STATUS_ID = "flightStatus"
    const val NEWS_ID = "news"
    const val GUEST_FEEDBACK_ID = "guestFeedback"
    const val TV_ON_OFF_ID = "tvonoff"
    const val LA_ID = "la"
    const val CONCIERGE_ID = "concierge"

    const val EXPRESS_CHECKOUT_ID = "expressCheckout"
    const val MESSAGE_ID = "message"

    //Guest Service Button Title
    const val WEATHER = "Weather"
    const val FLIGHT_STATUS = "Flight Status"
    const val NEWS = "News"
    const val GUEST_FEEDBACK = "Feedback"
    const val TV_ON_OFF = "TV On/Off Timer"
    const val LA = "Local Attractions"
    const val CONCIERGE = "Concierge"

    const val EXPRESS_CHECKOUT = "Express Checkout"
    const val MESSAGE = "Messages"


    val GUEST_SERVICE_BUTTON_LIST = listOf(
        GsBtnModel(WEATHER_ID, WEATHER, R.drawable.weather, R.drawable.weather_black),
        GsBtnModel(FLIGHT_STATUS_ID, FLIGHT_STATUS, R.drawable.flight_status, R.drawable.flight_status_black),
        GsBtnModel(NEWS_ID, NEWS, R.drawable.news, R.drawable.news_black),
        GsBtnModel(LA_ID, LA, R.drawable.local_attractions, R.drawable.local_attractions_black),

        GsBtnModel(EXPRESS_CHECKOUT_ID, EXPRESS_CHECKOUT, R.drawable.express_checkout, R.drawable.express_checkout_black),

        GsBtnModel(GUEST_FEEDBACK_ID, GUEST_FEEDBACK, R.drawable.guest_feedback, R.drawable.guest_feedback_black),
        GsBtnModel(MESSAGE_ID, MESSAGE, R.drawable.messages, R.drawable.messages_black),
        GsBtnModel(CONCIERGE_ID, CONCIERGE, R.drawable.concierge, R.drawable.concierge_black),
        GsBtnModel(FOOD_DELIVERY_ID, FOOD_DELIVERY, R.drawable.food_delivery_gs, R.drawable.weather_black),
        GsBtnModel(IN_ROOM_DINING_ID, IN_ROOM_DINING, R.drawable.in_room_dining1, R.drawable.in_room_dining1_black),
        GsBtnModel(TV_ON_OFF_ID, TV_ON_OFF, R.drawable.tv_on_off_timer, R.drawable.tv_on_off_timer_black)
    )

    //Concierge category name
    const val MAKE_MY_ROOM = "Make My Room"
    const val VALET_PARKING = "Valet Parking"
    const val LAUNDRY = "Laundry"
    const val TOILETRY_REQUEST = "Toiletry Request"
    const val SPA = "Spa"
    const val GOLF = "Golf"

    val CONCIERGE_BUTTON_LIST = listOf(
        ConciergeBtnModel(1, MAKE_MY_ROOM, R.drawable.make_my_room),
        ConciergeBtnModel(2, VALET_PARKING, R.drawable.valet_parking),
        ConciergeBtnModel(3, LAUNDRY, R.drawable.laundry),
        ConciergeBtnModel(4, TOILETRY_REQUEST, R.drawable.request_items),
        ConciergeBtnModel(5, SPA, R.drawable.spa),
        ConciergeBtnModel(6, GOLF, R.drawable.golf)
    )

    //Flight Status
    const val DEPARTURE = "DEP"
    const val ARRIVAL = "ARR"

    //Api Cmd
    const val ACTIVATE_CMD = "ACTIVATE"
    const val FLIGHT_STATUS_CMD = "FLSTATUS"

    //Colors
    const val COLOR_WHITE = "#FFFFFF"
    const val COLOR_BLACK = "#000000"
    const val COLOR_YELLOW = "#FFFF00"
    const val COLOR_ORANGE = "#FFA500"
    const val COLOR_GREEN = "#008000"

    //Weather
    const val SYMBOL_DEGREE_CELSIUS = " \u2103"
    const val SYMBOL_DEGREE_FAHRENHEIT = " \u2109"


}

const val SPLASH_DELAY = 3000
