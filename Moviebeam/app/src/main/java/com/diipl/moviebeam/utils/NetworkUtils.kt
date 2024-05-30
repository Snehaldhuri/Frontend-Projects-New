package com.diipl.moviebeam.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkUtils @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val TAG = "NetworkUtils"

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        val isInternet = actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

//        Log.e(TAG, "isNetworkAvailable: $isInternet")

        return when {
/*            isInternet -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true*/
             actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                     isInternet &&
                     (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) ||
                             actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
            else -> false
        }
    }

    val isNetworkConnected =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.isNetworkCapabilitiesValid() == true


    private fun NetworkCapabilities.isNetworkCapabilitiesValid(): Boolean = when {
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true

        else -> false
    }

    fun getConnectivityType(): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            return when {
                actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WIFI"
                actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "MOBILE DATA"
                actNw?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "LAN"
                actNw?.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) == true -> "BLUETOOTH"
                actNw?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true -> "VPN"
                else -> "UNKNOWN NETWORK"
            }
        } else {
            return when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> "WIFI"
                ConnectivityManager.TYPE_MOBILE -> "MOBILE DATA"
                ConnectivityManager.TYPE_ETHERNET -> "LAN"
                ConnectivityManager.TYPE_BLUETOOTH -> "BLUETOOTH"
                ConnectivityManager.TYPE_VPN -> "VPN"
                else -> "UNKNOWN NETWORK"
            }
        }
    }

}