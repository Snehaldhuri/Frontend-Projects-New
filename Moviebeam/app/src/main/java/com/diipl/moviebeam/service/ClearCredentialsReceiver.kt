package com.diipl.moviebeam.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.diipl.moviebeam.utils.clearCredentials

class ClearCredentialsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.clearCredentials()
    }

}