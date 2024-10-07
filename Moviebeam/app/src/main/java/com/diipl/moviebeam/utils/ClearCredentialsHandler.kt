package com.diipl.moviebeam.utils

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.PopupLayoutBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "ClearCredentialsHandler"
class ClearCredentialsHandler(private val context: Context, private val accountSetupDataStore: DataStore<AccountSetupResponse>) {

    private var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var appList = ArrayList<String>()
    private val activity: Activity by lazy { BaseActivity.currentActivity!! }

    init {
        Log.e(TAG, "init:   ${activity.javaClass.simpleName} -->  ${context.javaClass.simpleName}")
        preferenceDataStoreHelper = PreferenceDataStoreHelper(context)
        sortFreeAndSubscriptionApps()
    }

    fun startClearCredentials(showPopUp: Boolean = true) {
        sortFreeAndSubscriptionApps()
        context.clearCredentials(appList)
        if (showPopUp) activity.showPopup()
    }

    private fun sortFreeAndSubscriptionApps() = CoroutineScope(Dispatchers.IO).launch {
        val selectedAppsList = accountSetupDataStore.data.first().selectedAppsList
        selectedAppsList.forEach {
            if (it.forAndroid) {
                appList.add(it.value)
            }
        }

        val apiApps = selectedAppsList.map { it.value }.toSet()
        updateAppList(apiApps)

        Log.e(TAG, "sortFreeAndSubscriptionApps: ${selectedAppsList.size}")
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

    private fun Context.createSpannableString(): SpannableString {
        val text = "Press + to return to the Main Menu at any time."
        val spannableString = SpannableString(text)
        val drawable: Drawable = getDrawable(R.drawable.remote_home)!!

        drawable.setBounds(0, 0, 45, 32)

        val imageSpan = BottomAlignedImageSpan(drawable)

        // Replace the '+' character with the ImageSpan
        spannableString.setSpan(
            imageSpan,
            text.indexOf('+'),
            text.indexOf('+') + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    private fun updateAppList(appList: Set<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.APP_LIST_KEY,
                appList
            )
        }
    }

}

class BottomAlignedImageSpan(drawable: Drawable) : ImageSpan(drawable) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val drawable = drawable
        canvas.save()

        val transY = bottom - drawable.bounds.bottom // Align drawable to the bottom
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)

        canvas.restore()
    }
}