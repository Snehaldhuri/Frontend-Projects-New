package com.diipl.moviebeam.utils

object KapingConstants {

    const val KAP_CMD_DO_NOTHING = "00"
    const val KAP_CMD_POST_RENT_LOG = "01"
    const val KAP_CMD_POST_SYS_LOG = "02"
    const val KAP_CMD_ACCOUNT_ACTIVATE = "06"    //Account Setup Callback
    const val KAP_CMD_CHECK_IN = "07"
    const val KAP_CMD_CHECK_OUT = "08"
    const val KAP_CMD_REBOOT = "09"
    const val KAP_CMD_PRICE_UPDATE = "14"
    const val KAP_CMD_POST_UI_LOG = "15"
    const val KAP_CMD_SET_GUEST_NAME = "17"
    const val KAP_CMD_CONTENT_PRICE_UPDATE = "18"
    const val KAP_CMD_DAYPASS_PRICE_UPDATE = "19"
    const val KAP_CMD_SEND_SYS_INFO = "1F"
    const val KAP_CMD_GET_STB_SOFTWARE = "28"
    const val KAP_CMD_CONTENT_STATUS = "29"
    const val KAP_CMD_NETWORK_DW_SPEED = "33" //added for check network download speed
    const val KAP_CMD_THEME_CHANGE = "30" //Theme chenage command
    const val KAP_CMD_LA_CHANGE = "36" //LA package updated
    const val KAP_CMD_HS_CHANGE = "37" //HS package updated
    const val KAP_CMD_FETCH_SYNC_LIST = "26" //Fetch Sync List
    const val KAP_CMD_ENABLE_DISABLE_ADULT_CONTENT = "0B" //Enable/Disable Adult Content
    const val KAP_CMD_GET_EPG_DATA = "38" //Enable/Disable Adult Content
    const val KAP_CMD_GET_CHANNEL_LIST = "3A"  // Get channel List
    const val KAP_CMD_RESET_LG_STB = "39"  // Reset STB
    const val KAP_CMD_FETCH_SHOWTIME_DATA = "3C"
    const val KAP_CMD_GET_GUEST_MESSAGES = "3D"
    const val KAP_CMD_SYNC_RECENT_VIEWED = "3E"
    const val KAP_CMD_SYNC_ADULT_DAYPASS = "3F"
    const val KAP_CMD_IN_ROOM_CHANGE = "40"
    const val KAP_CMD_LAUNDRY_CHANGE = "41"
    const val KAP_CMD_GET_FOOD_DELIVERY_DATA = "42"
    const val KAP_CMD_GET_TICKER_MESSAGES = "43"

}