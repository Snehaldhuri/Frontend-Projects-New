package com.diipl.moviebeam.service.handler

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.databinding.PopupLayoutBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.clearCredentials
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.toVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "ClearCredentialsHandler"
class ClearCredentialsHandler(private val context: Context, private val accountSetupDataStore: DataStore<AccountSetupResponse>) {

    private var appList = ArrayList<String>()
    private val activity: Activity by lazy { BaseActivity.currentActivity!! }
    private var preferenceHandler : PreferenceHandler = PreferenceHandler(context)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    fun startClearCredentials(showPopUp: Boolean = true) {
        sortFreeAndSubscriptionApps()
        handler.postDelayed({
            context.clearCredentials(appList)
        }, 100)
        if (showPopUp) activity.showPopup()
    }

    private fun sortFreeAndSubscriptionApps() = coroutineScope.launch {
        appList.clear()
        val selectedAppsList = accountSetupDataStore.data.first().selectedAppsList
        selectedAppsList.forEach {
            if (it.forAndroid) {
                val isDigit = it.value.isDigitsOnly()
                if (!isDigit)
                    appList.add(it.value)
            }
        }
        preferenceHandler.updateDatastoreVariables(appList = appList.toSet())
    }

    private fun Activity.showPopup() {
        val builder = AlertDialog.Builder(this)
        val popupBinding = PopupLayoutBinding.inflate(layoutInflater)
        builder.setView(popupBinding.root)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.onBackPressedDispatcher.addCallback {
            dialog.dismiss()
        }

        popupBinding.root.post {
            popupBinding.tvPopupText.text = getString(R.string.app_world_clear_credentials_message)
            popupBinding.btnOk.requestFocus()
            popupBinding.btnOk.background = getGradientColor()

            popupBinding.btnOk.toVisible()
            popupBinding.lvHome.toVisible()
        }

        popupBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


}