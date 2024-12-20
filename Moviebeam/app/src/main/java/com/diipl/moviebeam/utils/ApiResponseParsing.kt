package com.diipl.moviebeam.utils

import com.google.gson.Gson
import kotlin.reflect.KClass

class ApiResponseParsing {

    private fun <T: Any> mapJsonToObj(json: String?, clazz: KClass<T>): T? {
        json?.let {
            return Gson().fromJson(it, clazz.java)
        }?: return null
    }

    fun <T : Any> getResponseAsObject(str: String?, clazz: KClass<T>): T? {
        // Ensure the string is not null and contains a callback before parsing
        return str?.let {
            val cleanedJson = extractJsonFromCallback(it)
            mapJsonToObj(cleanedJson, clazz)
        }
    }

    private fun extractJsonFromCallback(str: String?): String? {
        // Use a regular expression to extract JSON after the first '(' and before the last ')'
        val regex = Regex("""\((.*)\)""")
        val matchResult = regex.find(str ?: "") // Find content inside parentheses
        return matchResult?.groupValues?.get(1) // Return the content inside parentheses
    }

    private fun isCallbackFormat(str: String): Boolean {
        // Check if the string contains a callback-like structure
        return str.contains("(") && str.contains(")")
    }

    fun parseSysInfoResponse(response: String?): Int? {
        return response?.get(12)?.digitToInt()
    }

}