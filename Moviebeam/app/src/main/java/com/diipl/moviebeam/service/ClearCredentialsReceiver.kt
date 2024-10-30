package com.diipl.moviebeam.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.utils.ClearCredentialsHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClearCredentialsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>
    private lateinit var clearCredentialsHandler : ClearCredentialsHandler

    override fun onReceive(context: Context, intent: Intent?) {
        clearCredentialsHandler = ClearCredentialsHandler(context, accountSetupDataStore)
        clearCredentialsHandler.startClearCredentials(false)
    }

}