package com.diipl.moviebeam.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.utils.clearCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClearCredentialsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.Default).launch {
            context.clearCredentials(ArrayList(getAppList(PreferenceDataStoreHelper(context))))
        }
    }

    private suspend fun getAppList(preferenceDataStoreHelper: PreferenceDataStoreHelper): Set<String> {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.APP_LIST_KEY,
            emptySet()
        )
    }

}