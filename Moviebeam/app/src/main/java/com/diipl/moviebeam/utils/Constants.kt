package com.diipl.moviebeam.utils

import android.graphics.drawable.GradientDrawable
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.data.dto.btn.GsBtnModel
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO

object Constants {

    // WorkManager
    val DATA_TYPE = "dataType"
    val DATA_JSON = "dataJSON"

    var isWorkDone = 0
    var isRebooted = -1
    var GLOBAL_LOOP_SEC = 60

    // IP Details
    var IP_ADDRESS: String = "0.0.0.0"
    var IP_GATEWAY: String = "0.0.0.0"
    var IP_NET_MASK: String = "0.0.0.0"
    var CONNECTIVITY = "NO INTERNET"

    const val SHOWTIME_RELEASE_TYPE_ID = 1
    const val ENABLE = "ENABLE"
    const val DISABLE = "DISABLE"

    // Adult Pop-up viewType
    const val ADULT_MCW_MAIN = 1
    const val ADULT_MCW_BTN = 2
    const val ADULT_MCD_BTN = 3
    const val ADULT_CONTENT_DISABLED = 4
    const val ADULT_LOCKED = 5
    const val PARENTAL_CONTROL = 6

    // Content Count
    var MOVIES_COUNT = 0
    var SHOWS_COUNT = 0

    // Gradient Colors
    var GRADIENT_COLOR_START = ""
    var GRADIENT_COLOR_END = ""

    // Movie Rental
    const val C_TYPE_MOVIE: String = "MOVIE"
    const val C_TYPE_TRAILER: String = "TRAILER"
    var IS_CHECKED_IN: Boolean = false
    var SESSION_ID: String = ""
    var RENTAL_ID: String = ""
    var RENTAL_TIME: Long = 1707840000000
    var MOVIE_SELECTED_POSITION: Int = -1
    var MOVIE_PARENT_POSITION: Int = -1
    //    var isUserCheckedIn = false
//    var isUserCheckedIn = true
    const val SPLASH_DELAY = 3000
    var timer = ""
    const val BASE_URL_LG_REST =
        "https://stb.moviebeam.com:1930/LG/rest/"//https://stb.moviebeam.com:1930/LG/rest
    const val BASE_URL_ACCOUNT_SETUP = "https://stb.moviebeam.com:1926/"
    const val BASE_URL_MOVIE_RENTAL = "https://stb.moviebeam.com:1927/"
    const val BASE_URL_ASSET = "https://stb.moviebeam.com:1928/Asset/"
    const val API_TIME_OUT_IN_SEC = 60L
    const val INTERNET_ERROR_MESSAGE = "Internet Connection Not Available"
    const val SERVER_ERROR = "Server Error"
    const val LG_REST = "LG_REST"
    const val ACCOUNT_SETUP = "ACCOUNT_SETUP"
    const val HELP_INFO = "Help & Info"
    const val SYSTEM_INFO = "System Info"
    const val TAB_PARENTAL_CONTROL = "Parental Control"
    const val HOTEL_INFORMATION = "Hotel Information"
    const val DEFAULTGRADIENTSTARTCOLOR = "#85bf08"
    const val DEFAULTGRADIENTENDCOLOR = "#0ca654"
    var GRADIENT: GradientDrawable? = null
    var TITLE: String? = null
    var LOGO_IMAGE: String? = null
    var BG_IMAGE: String? = null
    var HOTEL_VIDEO_LOOP_COUNT = 3
    var HOTEL_VIDEO_DURATION = 0L
    var HOTEL_VIDEO_URL = ""
    var UA = ""
    var SERIAL_NO = ""
    val MAC_ADDRESS = "test"
    val STB_TYPE = "LG"
    val WIFI_MAC_ADDRESS = ""
    var IS_API_CALLED = false
    var ACCOUNT_ID = "13827"
    var STB_ROOM_NO = ""
    var C_LIST_VERSION = ""
    var NETFLIX_LAUNCHED = false
    const val ACTIVATE = "ACTIVATE"
    const val MODE = "JSON"
    const val THEME_DIRECTORY = "ThemeImages"
    const val HOTEL_LOGO = "HotelLogo.jpg"
    var BACKGROUND_IMAGE : String? = null
    var EPG_CDN_URL = ""
    const val EPG_CLOUD_URL_SUFFIX = "/epg_v3/HotelEPG.json"
    const val SERIAL_NO_PATH_SUFFIX = "/Documents/system/serialNo.txt"

    const val MDM_PACKAGE_NAME = "com.diipl.mdm"
    const val MDM_SERIAL_ACTIVITY = "$MDM_PACKAGE_NAME.ui.SerialActivity"
    const val MDM_CLEAR_CREDENTIALS_ACTION = "$MDM_PACKAGE_NAME.services.START_CLEAR_CREDENTIALS_RECEIVER"
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
        BtnModel(FOOD_DELIVERY_ID, R.drawable.fooddelivery_icon, FOOD_DELIVERY)
    )
    var APP_LIST: ArrayList<String> = ArrayList()
    const val APP_LIST_PARAM = "APP_LIST"
    const val CLEAR_CREDENTIALS_REQUEST_CODE = 10

    // Static Movies URl

    const val MOVIE_URL = "http://d3rh9vbn3pp0qe.cloudfront.net/41177_T.m2t"
    const val BASE_PLAYBACK_URL = "https://d14ez9fl8x9e1s.cloudfront.net/"
    const val TRAILER_EXTENSION = "_T.m2t"
    const val CONTENT_EXTENSION = ".m2t"
    const val CONTENT_EXTENSION_MP = ".mp4"

    /* const val MOVIE_URL1 = "https://d3rh9vbn3pp0qe.cloudfront.net/41177_T.ts"
     const val MOVIE_URL2 = "https://d3rh9vbn3pp0qe.cloudfront.net/41132_T.ts"
     const val MOVIE_URL3 = "https://d3rh9vbn3pp0qe.cloudfront.net/41191_T.ts"
     const val MOVIE_URL3 = "https://d3rh9vbn3pp0qe.cloudfront.net/41191_T.ts"*/
    const val MOVIE_URL1 =
        "https://s3.amazonaws.com/demohotelvideo.moviebeam.com/lgThemes/12974/hotelData/12974.mp4"
    const val MOVIE_URL2 =
        "https://s3.amazonaws.com/demohotelvideo.moviebeam.com/lgThemes/12974/hotelData/12974_B.mp4"
    const val MOVIE_URL3 =
        "https://s3.amazonaws.com/demohotelvideo.moviebeam.com/lgThemes/7107/hotelData/7107.mp4"
    const val TRAILER_URL = "trailer_url"

    const val RELEASE_ID = "releaseId"
    const val MOVIE_DETAILS = "movieDetails"
    const val SHOW_DETAILS = "showDetails"
    const val IS_TRAILER = "isTrailer"
    const val IS_CONTENT = "isContent"
    const val IS_CONTINUE = "isContinue"

    val RECENT_BUTTON = BtnModel(RECENT_WATCH_MOVIE_ID, R.drawable.img_recent_view, RECENTLY_VIEWED)
    val ADULT_DAY_PASS_BUTTON = BtnModel(ADULT_DAY_PASS_ID, R.drawable.adult_day_pass, ADULT_DAY_PASS)
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

    val MESSAGE_MODEL = GsBtnModel(MESSAGE_ID, MESSAGE, R.drawable.messages, R.drawable.messages_black)
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

    //        "Prime Video",
//        "Crackle",
//        "Spotify",
//        "Plex",
//        "Vudu Movies & TV",
//        "DAZN",
//        "Pac-12 Now",
//        "ESPN"
    //Guest Feedback
    const val UNACCEPTABLE = "Unacceptable"
    const val DISAPPOINTING = "Disappointing"
    const val GOOD = "Good"
    const val EXCELLENT = "Excellent"

    //    const val UNSATISFIED = " Unsatisfies"
    const val FEEDBACK_POSITIVE_COLOR = "#34C759"
    const val FEEDBACK_NEGATIVE_COLOR = "#FF453A"

    //Casting
    var CASTING_URL: String? = null

    // Kaping
    const val KAPING = "KAPING"
    const val DV = "3.0.1.23"
    const val CLISTVER = "201803130001"
    const val RBTY = "0100"
    const val LAVER = "1510818301"
    const val HSVER = "1513951822"
    const val THMVER = "1509529572"
    const val INRMVER = "1520924235"
    const val LAUVER = "1520937775"

    //Program Guide
    var CHANNEL_COUNT = 0
    const val CONTENT_LIST_PARAM = "contentList"
    const val CHANNEL_LIST_PARAM = "channelList"
    const val SELECTED_CHANNEL_INDEX = "index"
    const val CHANEL_NO_PARAM = "channelNo"
    const val CHANNEL_NAME_PARAM = "channelName"
    const val CHANNEL_LOGO_PARAM = "channelLogo"
    const val NOW_SHOWING_PARAM = "nowShowing"
    const val NEXT_PROGRAM_PARAM = "nextProgram"
    const val PROG_1_TIME_PARAM = "prog1Time"
    const val PROG_2_TIME_PARAM = "prog2Time"
    const val NO_INFORMATION_AVAILABLE = "No Information Available"

    const val GRADIENT_START_COLOR_PARAM = "gradientStartColor"
    const val GRADIENT_END_COLOR_PARAM = "gradientEndColor"
    const val TITLE_PARAM = "title"
    const val BG_IMAGE_URL = "bgImageUrl"
    const val LOGO_IMAGE_URL = "logoImageUrl"
    var EPG_START = ""
    var EPG_END = ""

    var NEXT_BUTTON_STATE = 0

    var CURRENT_PROGRAMS: List<ChannelEpgDTO>? = null
    const val TICKER_DTO_PARAM = "TICKER_DTO"
    const val TICKER_MESSAGE_DATE_FORMAT = "dd-MMM-yyyy hh:mm a"
    const val CHECK_OUT_TIME_DATE_FORMAT = "hh:mm a"

    const val BUILD_TYPE_CHROMECAST = "CHROMECAST"
    const val BUILD_TYPE_STB = "STB"
    const val BUILD_TYPE_MINI_BOX = "MINI_BOX"

    const val DESCRIPTION_PARAM = "desc"
    const val SERVICE_TYPE_CAROUSEL = 1
    const val SERVICE_TYPE_SERVICE_INFO = 2
    const val SERVICE_TYPE_HELP_INFO = 3

    const val SERVICE_IMAGE_LIST_PARAM = "SERVICE_IMAGE_LIST"
}

