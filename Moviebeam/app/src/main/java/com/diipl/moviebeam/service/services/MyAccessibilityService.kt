package com.diipl.moviebeam.service.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.currentActivity
import com.diipl.moviebeam.ui.player.ExoPlayerActivity
import com.diipl.moviebeam.ui.refreshingui.RefreshingUiActivity
import com.diipl.moviebeam.ui.register_stb.RegisterSTBActivity
import com.diipl.moviebeam.ui.serial_info.SerialActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.Constants.KEYCODE_EXIT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MyAccessibilityService"

@AndroidEntryPoint
class MyAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var accountDataStore: DataStore<AccountSetupResponse>

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var currentPackageName: String? = ""

    var basePlayBoxLiveTVUrl: String = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e(TAG, "Service connected")

        // Configure the service to listen for key events
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }

        this.serviceInfo = info
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        Log.e(
            TAG,
            "onKeyEvent: keycode ${event?.keyCode} action ${event?.action} ${event?.displayLabel} ${currentActivity?.localClassName} scancode ${event?.scanCode} repeatcount: ${event?.repeatCount}"
        )

        // Null exception for empty list to perform default intended action
        if (activityStack.isEmpty()) {
            return false
        }

        // Define the restricted activity names
        val restrictedActivities = setOf(
            SerialActivity::class.java.simpleName,
            STBDetailsActivity::class.java.simpleName,
            RefreshingUiActivity::class.java.simpleName,
            RegisterSTBActivity::class.java.simpleName,
            ExoPlayerActivity::class.java.simpleName
        )

        // Define the activities where DPAD navigation should still work
        val dpadEnabledActivities = setOf(
            STBDetailsActivity::class.java.simpleName,
            RegisterSTBActivity::class.java.simpleName,
            //MainMenuActivity::class.java.simpleName
        )

        // Check if we are in a restricted activity
        val currentActivityName = activityStack.last()
        if (currentActivityName in restrictedActivities) {
            // If the keycode is 83, 288, or 172, disable the functionality on restricted screens
//            if (event?.keyCode == KEYCODE_HOTEL_INFO || event?.keyCode == KEYCODE_GUEST_SERVICES || event?.keyCode == KEYCODE_PLAYBOX_TV || event?.keyCode == KEYCODE_BOOSTER) {
//                return true // Consume the event to prevent the specific functionality
//            }

            // Allow DPAD buttons (keycodes for DPAD navigation: left, right, up, down, center) on specific activities
            if (currentActivityName in dpadEnabledActivities &&
                (event?.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                        event?.keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
                        event?.keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                        event?.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
                        event?.keyCode == KeyEvent.KEYCODE_DPAD_CENTER||
                        event?.keyCode == KeyEvent.KEYCODE_BACK)
            ) {
                return false // Let DPAD events propagate with default functionality
            }

            return false // Consume other events in restricted activities
        }

        Log.e(TAG,"packageName $currentPackageName")

        // Handle specific key events globally for non-restricted activities
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KEYCODE_EXIT -> {
                    Log.d(TAG, "onKeyEvent: exit key pressed")
                    if (currentPackageName == "com.diipl.moviebeam" || currentPackageName == "org.dtvkit.inputsource") {
                        Log.d(TAG, "In moviebeam, allowing normal behavior")
                        return false
                    } else {
                        Log.e(TAG, "Exiting from $currentPackageName to Home")
                        performGlobalAction(GLOBAL_ACTION_HOME)
                        return true
                    }
                }
                else -> {
                    Log.d(TAG, "onKeyEvent: chaitali dhuri")
                }
            }
        }

        return false // Let default processing handle non-ACTION_DOWN events
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.packageName?.let {
            val packageName = it.toString()

            Log.d(TAG, "onAccessibilityEvent: package = $packageName")

            if (packageName != currentPackageName) {
                currentPackageName = packageName
                Log.d(TAG, "Updated currentPackageName: $currentPackageName")
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruption if necessary
    }

}
