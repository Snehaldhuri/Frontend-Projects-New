package com.diipl.moviebeam.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    val IS_MINOR_KEY = booleanPreferencesKey("IS_MINOR_KEY")
    val AGE_KEY = intPreferencesKey("AGE_KEY")
    val NAME_KEY = stringPreferencesKey("NAME_KEY")
    val MOBILE_NUMBER = longPreferencesKey("MOBILE_NUMBER")

    val IS_SERIAL_NO_TAKEN_KEY = booleanPreferencesKey("IS_SERIAL_NO_TAKEN_KEY")
    val SERIAL_NO_KEY = stringPreferencesKey("SERIAL_NO_KEY")
    val SERIAL_NO = stringPreferencesKey("SerialNo")
    val ADULT_CONTENT_STATUS = booleanPreferencesKey("adultContentStatus")
    val ADULT_DAY_PASS_STATUS = booleanPreferencesKey("adultDayPassStatus")
    val ADULT_DAY_PASS_FINISH_TIME = longPreferencesKey("adultDayPassFinishTime")
    val UA = stringPreferencesKey("UA")


    // Kaping Keys
    val IS_GUEST_CHECKED_IN = booleanPreferencesKey("IS_GUEST_CHECKED_IN")
    val GUEST_DETAILS = stringPreferencesKey("GUEST_DETAILS")

}