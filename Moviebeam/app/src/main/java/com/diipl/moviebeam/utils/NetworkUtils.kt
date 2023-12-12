package com.diipl.moviebeam.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkUtils @Inject constructor(
    @ApplicationContext val context : Context
) {
    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)  as ConnectivityManager

    /*val isNetworkConnected: Boolean
        @RequiresApi(Build.VERSION_CODES.M)
        get() = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            .isNetworkCapabilitiesValid()*/

    val isNetworkConnected : Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            .isNetworkCapabilitiesValid()
    }else {
        connectivityManager.activeNetworkInfo?.isConnected ?: false
    }


    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean = when {
        this == null -> false
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true
        else -> false
    }

}