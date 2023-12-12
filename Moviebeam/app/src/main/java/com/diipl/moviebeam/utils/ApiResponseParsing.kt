package com.diipl.moviebeam.utils

import com.google.gson.Gson
import kotlin.reflect.KClass

class ApiResponseParsing {

    fun <T: Any> getResponseAsObject(str: String?, clazz: KClass<T>): T? {
        return mapJsonToObj(callbackToJson(str), clazz)
    }

    private fun callbackToJson(str: String?): String?{
        return str?.substring(10, str.length-3)
    }

    private fun <T: Any> mapJsonToObj(json: String?, clazz: KClass<T>): T? {
        json?.let {
            return Gson().fromJson(it, clazz.java)
        }?: return null
    }
}