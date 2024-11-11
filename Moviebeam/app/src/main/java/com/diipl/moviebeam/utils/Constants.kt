package com.diipl.moviebeam.utils

import android.view.KeyEvent
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.data.dto.btn.GsBtnModel

object Constants {

    const val CONFIG_DATA_KEY = "configData"

    var isWorkDone = 0
    const val GLOBAL_LOOP_SEC = 60

    const val SHOWTIME_RELEASE_TYPE_ID = 1

    // Adult Pop-up viewType
    const val ADULT_MCW_MAIN = 1
    const val ADULT_MCW_BTN = 2
    const val ADULT_MCD_BTN = 3
    const val ADULT_CONTENT_DISABLED = 4
    const val ADULT_LOCKED = 5
    const val PARENTAL_CONTROL = 6

    //Keycodes For Remote
    const val GUIDE_KEY = 67
    const val APP_WORLD_KEY = 68
    const val LIVE_TV_KEY = 70
    const val CASTING_KEY = 119
    const val EXIT_KEY = 61
    const val PROGRAM_SEARCH_KEY = 64
    const val NETFLIX_KEY = 65
    const val YOUTUBE_KEY = 66

    // KeyCode for SEI Remote
    const val ATV_EXIT_KEYCODE = KeyEvent.KEYCODE_F3
    const val ATV_SEARCH_KEYCODE = KeyEvent.KEYCODE_F6
    const val ATV_NETFLIX_KEYCODE = KeyEvent.KEYCODE_F7
    const val ATV_YOUTUBE_KEYCODE = KeyEvent.KEYCODE_F8
    const val ATV_GUIDE_KEYCODE = KeyEvent.KEYCODE_F9
    const val ATV_APPS_KEYCODE = KeyEvent.KEYCODE_F10
    const val ATV_LIVE_TV_KEYCODE = KeyEvent.KEYCODE_SCROLL_LOCK
    const val ATV_CASTING_KEYCODE = KeyEvent.KEYCODE_BREAK
    const val ATV_PRIME_VIDEO_KEYCODE = KeyEvent.KEYCODE_PROG_GREEN
    const val ATV_LAST_CHANNEL_KEYCODE = KeyEvent.KEYCODE_LAST_CHANNEL
    const val ATV_CAPTIONS_KEYCODE = KeyEvent.KEYCODE_CAPTIONS
    const val ATV_BLUE_KEYCODE = KeyEvent.KEYCODE_PROG_YELLOW
    const val ATV_SETTINGS_KEYCODE = KeyEvent.KEYCODE_PROG_BLUE


    // Movie Rental
    const val C_TYPE_MOVIE: String = "MOVIE"
    const val C_TYPE_TRAILER: String = "TRAILER"
    const val SPLASH_DELAY = 3000
    const val BASE_URL_LG_REST =
        "https://stb.moviebeam.com:1930/LG/rest/"//https://stb.moviebeam.com:1930/LG/rest
    const val BASE_URL_ACCOUNT_SETUP = "https://stb.moviebeam.com:1926/"
    const val BASE_URL_MOVIE_RENTAL = "https://stb.moviebeam.com:1927/"
    const val BASE_URL_ASSET = "https://stb.moviebeam.com:1928/Asset/"
    const val API_TIME_OUT_IN_SEC = 30L
    const val INTERNET_ERROR_MESSAGE = "Internet Connection Not Available"
    const val SERVER_ERROR = "Server Error"
    const val LG_REST = "LG_REST"
    const val ACCOUNT_SETUP = "ACCOUNT_SETUP"
    const val ALL_SETUP = "ALL_SETUP"
    const val HELP_INFO = "Help & Info"
    const val SYSTEM_INFO = "System Info"
    const val TAB_PARENTAL_CONTROL = "Parental Control"
    const val HOTEL_INFORMATION = "Hotel Information"
    const val DEFAULTGRADIENTSTARTCOLOR = "#85bf08"
    const val DEFAULTGRADIENTENDCOLOR = "#0ca654"
    const val UA_PREFIX = "21"

    const val MAC_ADDRESS = "test"
    const val STB_TYPE = "LG"
    const val WIFI_MAC_ADDRESS = ""
    const val ACTIVATE = "ACTIVATE"
    const val MODE = "JSON"
    const val EPG_CLOUD_URL_SUFFIX = "/epg_v3/HotelEPG.json"

    const val NETFLIX_PACKAGE_NAME = "com.netflix.ninja"
    const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube.tv"

    const val PRIME_VIDEO_PACKAGE_NAME = "com.amazon.amazonvideo.livingroom"
    const val MDM_PACKAGE_NAME = "com.diipl.mdm"
    const val PBTV_PACKAGE_NAME = "playboxtv.tv.android.in"
    const val MDM_SERIAL_ACTIVITY = "$MDM_PACKAGE_NAME.ui.SerialActivity"
    const val MDM_CLEAR_CREDENTIALS_ACTION =
        "$MDM_PACKAGE_NAME.services.START_CLEAR_CREDENTIALS_RECEIVER"
    const val MDM_GRANT_PERMISSION = "$MDM_PACKAGE_NAME.ui.GrantPermission"
    const val MDM_UPDATE_DATA = "$MDM_PACKAGE_NAME.ui.UpdateData"
    const val SERIAL_NO_KEY = "SERIAL_NO_KEY"

    const val ASSET = "ASSET"
    const val MOVIE_ACCESS = "MOVIE_ACCESS"
    const val EPG = "EPG"
    const val SYS_INFO = "SYS_INFO"

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
    const val MAIN_WEATHER_ID = "mainWeather"
    const val MAIN_GUEST_MSG_ID = "mainmsg"
    const val MAIN_FEEDBACK_ID = "mainFeedback"
    const val MAIN_NEWS_ID = "mNews"
    const val CONCIERGE_MAIN_ID = "conm"


    //Home Page Menu Button Title
//    const val PROGRAM_GUIDE = "Program Guide"
    const val PROGRAM_GUIDE = "Live TV"
    const val MOVIES_MORE = "Movies & More"
    const val SHOWTIME_NAME = "Free Selections"
    const val SHOWTIME = "Showtime"
    const val CASTING = "Casting"
    const val APPS = "Apps"
    const val GUEST_SERVICES = "Guest Services"
    const val HOTEL_SERVICES = "Hotel Info"
    const val CRACKLE_DEFAULT = "Crackle"
    const val IN_ROOM_DINING = "In Room Dining"
    const val LOCAL_ATTRACTION = "Local Attractions"
    const val FOOD_DELIVERY = "Food Delivery"
    const val MAIN_GUEST_MSG = "Message"
    const val MAIN_FEEDBACK = "Guest Feedback"
    const val MAIN_CONCIERGE ="Concierge"
    const val MAIN_WEATHER ="Weather"
    const val MAIN_NEWS = "News"

    //Movies page menu button Id
    const val MOVIE_RENTALS_ID = "movieRentals"
    const val FREE_MOVIES_ID = "freeMovies"
    const val RECENT_WATCH_MOVIE_ID = "recentWatch"
    const val ADULT_DAY_PASS_ID = "adultDayPass"
    const val ADULT_ID = "adult"
    const val ALL_PAY_MOVIES = "All Pay Movies"

    //Movies page menu button list
    const val MOVIE_RENTALS = "Movie Rentals"
    const val FREE_VOD = "Free Movies"
    const val ADULT_DAY_PASS = "Adult Day Pass"
    const val ADULT = "Adult"
    const val RECENTLY_VIEWED = "Recently Viewed"

    //Showtime page menu button list
    const val ALL_SHOWS = "All Shows"
    const val SHO_SPORTS = "SHO Sports"
    const val SHO_SERIES = "SHO Series"
    const val SHO_DOCS = "SHO Docs"

    //Showtime page menu button Id
    const val ALL_SHOWS_ID = "allShows"
    const val SHO_SPORTS_ID = "shoSports"
    const val SHO_SERIES_ID = "shoSeries"
    const val SHO_DOCS_ID = "shoDocs"

    val MENU_MESSAGE_MODEL =
        BtnModel(MAIN_GUEST_MSG_ID, R.drawable.messages, MAIN_GUEST_MSG)

    val HOME_PAGE_MENU_BUTTON_LIST = listOf(
        BtnModel(VOD_ID, R.drawable.video_on_demand_icon, MOVIES_MORE),
        BtnModel(PRG_GUIDE_ID, R.drawable.program_guide_icon, PROGRAM_GUIDE),
        BtnModel(SHOWTIMES_ID, R.drawable.showtime_icon, SHOWTIME_NAME),
        BtnModel(CASTING_ID, R.drawable.casting_icon, CASTING),
        BtnModel(APPS_ID, R.drawable.app_world_icon, APPS),
        BtnModel(GUEST_SERVICES_ID, R.drawable.guestservices_icon, GUEST_SERVICES),
        BtnModel(HOTEL_SERVICES_ID, R.drawable.hotelservices_icon, HOTEL_SERVICES),
        BtnModel(CRACKLE_DEFAULT_ID, R.drawable.crackle_white_icon, CRACKLE_DEFAULT),
        BtnModel(IN_ROOM_DINING_ID, R.drawable.in_room_dining_menu, IN_ROOM_DINING),
        BtnModel(LOCAL_ATTRACTION_ID, R.drawable.localattraction_icon, LOCAL_ATTRACTION),
        BtnModel(FOOD_DELIVERY_ID, R.drawable.fooddelivery_icon, FOOD_DELIVERY),
        BtnModel(MAIN_WEATHER_ID, R.drawable.weather, MAIN_WEATHER),
        MENU_MESSAGE_MODEL,
        BtnModel(MAIN_FEEDBACK_ID, R.drawable.guest_feedback, MAIN_FEEDBACK) ,
        BtnModel(MAIN_NEWS_ID, R.drawable.news, MAIN_NEWS),
        BtnModel(CONCIERGE_MAIN_ID, R.drawable.concierge_icon_white, MAIN_CONCIERGE),

        )
    const val APP_LIST_PARAM = "APP_LIST"
    const val CLEAR_CREDENTIALS_REQUEST_CODE = 10

    // Static Movies URl

    const val BASE_PLAYBACK_URL = "https://d14ez9fl8x9e1s.cloudfront.net/"
    const val TRAILER_EXTENSION = "_T.mp4"
    const val CONTENT_EXTENSION = ".mp4"

    const val RELEASE_ID = "releaseId"
    const val MOVIE_DETAILS = "movieDetails"
    const val SHOW_DETAILS = "showDetails"
    const val IS_TRAILER = "isTrailer"
    const val IS_CONTENT = "isContent"
    const val IS_CONTINUE = "isContinue"

    val RECENT_BUTTON = BtnModel(RECENT_WATCH_MOVIE_ID, R.drawable.img_recent_view, RECENTLY_VIEWED)
    val ADULT_DAY_PASS_BUTTON =
        BtnModel(ADULT_DAY_PASS_ID, R.drawable.adult_day_pass, ADULT_DAY_PASS)
    val ADULT_BUTTON = BtnModel(ADULT_ID, R.drawable.adult, ADULT)

    val MOVIES_PAGE_MENU_BUTTON_LIST = mutableListOf(
        BtnModel(MOVIE_RENTALS_ID, R.drawable.movie_rentals_img, MOVIE_RENTALS),
        BtnModel(FREE_MOVIES_ID, R.drawable.video_on_demand_icon, FREE_VOD),
        ADULT_DAY_PASS_BUTTON,
        ADULT_BUTTON,
        RECENT_BUTTON
    )
    val SHOWTIME_PAGE_MENU_BUTTON_LIST = listOf(
        BtnModel(ALL_SHOWS_ID, R.drawable.showtime, ALL_SHOWS),
        BtnModel(SHO_SPORTS_ID, R.drawable.showtime, SHO_SPORTS),
        BtnModel(SHO_SERIES_ID, R.drawable.showtime, SHO_SERIES),
        BtnModel(SHO_DOCS_ID, R.drawable.showtime, SHO_DOCS),
    )

    const val FREE_MOVIE_RELEASE_TYPE_ID = 1
    const val PAID_MOVIE_RELEASE_TYPE_ID = 2

    //Guest Service Button Id
    const val ALL_SERVICES = "ALL_SERVICES"
    const val WEATHER_ID = "weather"
    const val FLIGHT_STATUS_ID = "flightStatus"
    const val NEWS_ID = "news"
    const val GUEST_FEEDBACK_ID = "guestFeedback"
    const val TV_ON_OFF_ID = "tvonoff"
    const val LA_ID = "la"
    const val IN_ROOM_ID = "inRoomDining"
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

    val MESSAGE_MODEL =
        GsBtnModel(MESSAGE_ID, MESSAGE, R.drawable.messages, R.drawable.messages_black)


    val GUEST_SERVICE_BUTTON_LIST = listOf(
        GsBtnModel(WEATHER_ID, WEATHER, R.drawable.weather, R.drawable.weather_black),
        GsBtnModel(
            FLIGHT_STATUS_ID,
            FLIGHT_STATUS,
            R.drawable.flight_status,
            R.drawable.flight_status_black
        ),
        GsBtnModel(NEWS_ID, NEWS, R.drawable.news, R.drawable.news_black),
        GsBtnModel(LA_ID, LA, R.drawable.local_attractions, R.drawable.local_attractions_black),

        GsBtnModel(
            EXPRESS_CHECKOUT_ID,
            EXPRESS_CHECKOUT,
            R.drawable.express_checkout,
            R.drawable.express_checkout_black
        ),

        GsBtnModel(
            GUEST_FEEDBACK_ID,
            GUEST_FEEDBACK,
            R.drawable.guest_feedback,
            R.drawable.guest_feedback_black
        ),
        MESSAGE_MODEL,
        GsBtnModel(CONCIERGE_ID, CONCIERGE, R.drawable.concierge, R.drawable.concierge_black),
        GsBtnModel(
            FOOD_DELIVERY_ID,
            FOOD_DELIVERY,
            R.drawable.food_delivery_gs,
            R.drawable.weather_black
        ),
        GsBtnModel(
            IN_ROOM_DINING_ID,
            IN_ROOM_DINING,
            R.drawable.in_room_dining1,
            R.drawable.in_room_dining1_black
        ),
        GsBtnModel(
            TV_ON_OFF_ID,
            TV_ON_OFF,
            R.drawable.tv_on_off_timer,
            R.drawable.tv_on_off_timer_black
        )
    )

    //Concierge category name
    const val MAKE_MY_ROOM = "Make My Room"
    const val VALET_PARKING = "Valet Parking"
    const val LAUNDRY = "Laundry"
    const val TOILETRY_REQUEST = "Toiletry Request"
    const val SPA = "Spa"
    const val GOLF = "Golf"
    const val LAUNDRY_TIME = "Laundry Time"

    val CONCIERGE_BUTTON_LIST = listOf(
        ConciergeBtnModel(1, MAKE_MY_ROOM, R.drawable.make_my_room),
        ConciergeBtnModel(2, VALET_PARKING, R.drawable.valet_parking),
        ConciergeBtnModel(3, LAUNDRY, R.drawable.laundry),
        ConciergeBtnModel(4, TOILETRY_REQUEST, R.drawable.request_items),
        ConciergeBtnModel(5, SPA, R.drawable.spa),
        ConciergeBtnModel(6, GOLF, R.drawable.golf),
        ConciergeBtnModel(7, LAUNDRY_TIME, R.drawable.laundry)
    )

    //Flight Status
    const val DEPARTURE = "DEP"
    const val ARRIVAL = "ARR"
    const val NOT_AVAILABLE = "N/A"

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

    //Guest Feedback
    const val UNACCEPTABLE = "Unacceptable"
    const val DISAPPOINTING = "Disappointing"
    const val AVERAGE = "Average"
    const val GOOD = "Good"
    const val EXCELLENT = "Excellent"

    //    const val UNSATISFIED = " Unsatisfies"
    const val FEEDBACK_POSITIVE_COLOR = "#34C759"
    const val FEEDBACK_NEGATIVE_COLOR = "#FF453A"

    // Kaping
    const val KAPING = "KAPING"
    const val DV = "3.0.1.23"
    const val RBTY = "0100"
    const val INRMVER = "1520924235"
    const val LAUVER = "1520937775"

    //Program Guide
    const val CONTENT_LIST_PARAM = "contentList"
    const val SELECTED_CHANNEL_INDEX = "index"
    const val NO_INFORMATION_AVAILABLE = "No Information Available"

    const val TICKER_DTO_PARAM = "TICKER_DTO"
    const val TICKER_MESSAGE_DATE_FORMAT = "dd-MMM-yyyy hh:mm a"
    const val CHECK_OUT_TIME_DATE_FORMAT = "hh:mm a"
    const val EPG_DATE_FORMAT = "dd-MMM-yyyy hh:mm a"

    const val BUILD_TYPE_CHROMECAST = "CHROMECAST"
    const val BUILD_TYPE_STB = "STB"
    const val BUILD_TYPE_MINI_BOX = "MINI_BOX"

    const val SERVICE_TYPE_CAROUSEL = 1
    const val SERVICE_TYPE_SERVICE_INFO = 2
    const val SERVICE_TYPE_HELP_INFO = 3

    const val SERVICE_IMAGE_LIST_PARAM = "SERVICE_IMAGE_LIST"

    const val EPG_API_CALL_TIME_INTERVAL_HOURS: Long = 8


    const val DTV_KIT_PACKAGE_NAME = "org.dtvkit.inputsource"
    var DTV_INPUT_ID = "$DTV_KIT_PACKAGE_NAME/.DtvkitTvInput/HW19"

    const val SEI_MB730 = "MB730"
    const val HOTEL_VIDEO = "Hotel Video"

    const val PLAY_MEDIA_BACKWARD= 168
    const val MEDIA_PLAY_PAUSE= 164
    const val PLAY_MEDIA_FORWARD= 208

}