package com.diipl.moviebeam.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceDataStoreConstants {
    val IS_MINOR_KEY = booleanPreferencesKey("IS_MINOR_KEY")
    val AGE_KEY = intPreferencesKey("AGE_KEY")
    val NAME_KEY = stringPreferencesKey("NAME_KEY")
    val MOBILE_NUMBER = longPreferencesKey("MOBILE_NUMBER")

    val IS_SERIAL_NO_TAKEN_KEY = booleanPreferencesKey("IS_SERIAL_NO_TAKEN_KEY")
    val SERIAL_NO_KEY = stringPreferencesKey("SERIAL_NO_KEY")
    val SERIAL_NO = stringPreferencesKey("SerialNo")
    val ADULT_CONTENT_STATUS = booleanPreferencesKey("adultContentStatus")
    val NETWORK_STATUS = booleanPreferencesKey("NETWORK_STATUS")
    val ADULT_DAY_PASS_STATUS = booleanPreferencesKey("adultDayPassStatus")
    val ADULT_DAY_PASS_FINISH_TIME = longPreferencesKey("adultDayPassFinishTime")
    val UA = stringPreferencesKey("UA")


    // Kaping Keys
    val IS_GUEST_CHECKED_IN_KEY = booleanPreferencesKey("IS_GUEST_CHECKED_IN")
    val GUEST_DETAILS = stringPreferencesKey("GUEST_DETAILS")

    val IS_STB_REGISTERED = booleanPreferencesKey("IS_STB_REGISTERED")
    val IS_STB_ALLOCATED = booleanPreferencesKey("IS_STB_ALLOCATED")

    val ACCOUNT_ID_KEY = stringPreferencesKey("ACCOUNT_ID")
    val STB_ROOM_NO_KEY = stringPreferencesKey("STB_ROOM_NO")
    val EPG_CDN_URL_KEY = stringPreferencesKey("EPG_CDN_URL")
    val CASTING_URL_KEY = stringPreferencesKey("CASTING_URL")
    val HOTEL_VIDEO_URL_KEY = stringPreferencesKey("HOTEL_VIDEO_URL")
    val SHOWS_COUNT_KEY = intPreferencesKey("SHOWS_COUNT")
    val MOVIES_COUNT_KEY = intPreferencesKey("MOVIES_COUNT")
    val C_LIST_VERSION_KEY = stringPreferencesKey("C_LIST_VERSION")
    val IP_ADDRESS_KEY = stringPreferencesKey("IP_ADDRESS")
    val IP_GATEWAY_KEY = stringPreferencesKey("IP_GATEWAY")
    val IP_NET_MASK_KEY = stringPreferencesKey("IP_NET_MASK")
    val CONNECTIVITY_KEY = stringPreferencesKey("CONNECTIVITY")
    val GRADIENT_COLOR_START_KEY = stringPreferencesKey("GRADIENT_COLOR_START")
    val GRADIENT_COLOR_END_KEY = stringPreferencesKey("GRADIENT_COLOR_END")
    val CHANNEL_COUNT_KEY = intPreferencesKey("CHANNEL_COUNT")
    val EPG_START_TIME_KEY = stringPreferencesKey("EPG_START_TIME")
    val EPG_END_TIME_KEY = stringPreferencesKey("EPG_END_TIME")
    val APP_LIST_KEY = stringSetPreferencesKey("APP_LIST")
    val SESSION_ID_KEY = stringPreferencesKey("SESSION_ID")

}