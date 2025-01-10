package com.diipl.moviebeam.ui.base

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.Buttons
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.casting.CastingActivity
import com.diipl.moviebeam.ui.casting.HotspotActivity
import com.diipl.moviebeam.ui.concierge.ConciergeActivity
import com.diipl.moviebeam.ui.guest.feedback.GuestFeedbackActivity
import com.diipl.moviebeam.ui.guest.message.GuestMessageActivity
import com.diipl.moviebeam.ui.guest.news.NewsActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.inroomdining.InRoomDiningActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.newprogramguide.NewProgramGuideActivity
import com.diipl.moviebeam.ui.player.ExoPlayerActivity
import com.diipl.moviebeam.ui.player.LiveTVActivity
import com.diipl.moviebeam.ui.player.PlayerActivity
import com.diipl.moviebeam.ui.programguide.DisconnectedPrgActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.weather.WeatherActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.isNotEmptyOrNull
import com.diipl.moviebeam.utils.isPackageExists
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.setIPInfo
import com.diipl.moviebeam.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

private const val TAG = "BaseActivity"
@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    private var castingUrl = ""

    private val preferenceDataStoreHelper by lazy { PreferenceDataStoreHelper(this) }

    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var accountDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    val mainMenuButtonList: MutableList<Buttons> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = this

        launchLogger()
        initViewBinding()
        observeViewModel()
        this.initializeDatastoreParams()

        logD("${this::class.java.simpleName} started.")
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            castingUrl = preferenceHandler.castingUrl
            mainMenuButtonList.addAll(accountDataStore.data.first().buttonsList)
        }
    }

    private fun handleBackKeyAndExitKey(): Boolean {
        Log.d(TAG, "handleBackKeyAndExitKey: $currentActivity")
        if (currentActivity is ShowtimeActivity) {
            (currentActivity as ShowtimeActivity).handleBackClick()
            return true
        }
        if (currentActivity is MoviesActivity) {
            (currentActivity as MoviesActivity).handleBackClick()
            return true
        }
        if (currentActivity is HotelInfoActivity) {
            (currentActivity as HotelInfoActivity).handleBackClick()
            return true
        }
        if (currentActivity is GuestServiceActivity) {
            (currentActivity as GuestServiceActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is AppWorldActivity) {
            (currentActivity as AppWorldActivity).handleBackClick()
            return true
        }
        if (currentActivity is WeatherActivity) {
            (currentActivity as WeatherActivity).handleBackClick()
            return true
        }
        if (currentActivity is NewsActivity) {
            (currentActivity as NewsActivity).handleBackClick()
            return true
        }
        if (currentActivity is ConciergeActivity) {
            (currentActivity as ConciergeActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is GuestFeedbackActivity) {
            (currentActivity as GuestFeedbackActivity).handleBackClick()
            return true
        }
        if (currentActivity is NewProgramGuideActivity) {
            Log.d(TAG, "handleBackKeyAndExitKey: issue found")
            (currentActivity as NewProgramGuideActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is GuestMessageActivity) {
            (currentActivity as GuestMessageActivity).handleBackClick()
            return true
        }
        if (currentActivity is InRoomDiningActivity) {
            (currentActivity as InRoomDiningActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is CastingActivity) {
            (currentActivity as CastingActivity).handleBackClick()
            return true
        }
        if (currentActivity is HotspotActivity) {
            (currentActivity as HotspotActivity).handleBackClick()
            return true
        }
        if (currentActivity is ExoPlayerActivity) {
            (currentActivity as ExoPlayerActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is DisconnectedPrgActivity) {
            (currentActivity as DisconnectedPrgActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is MainMenuActivity) {
            return true
        }
        if (currentActivity is LiveTVActivity) {
            Log.e(TAG, "Handling BACK key in LiveTVActivity")
            (currentActivity as LiveTVActivity).handleBackRemoteClick()
            return true
        }
        if (currentActivity is PlayerActivity) {
            (currentActivity as PlayerActivity).handleBackRemoteClick()
            return true
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        Log.d(
            TAG, "OnKeyDown-> keycode: $keyCode keyCode ${keyEvent.keyCode} action " +
                    "${keyEvent.action} displayLabel ${keyEvent.displayLabel}  number ${keyEvent.number} " +
                    "scanCode ${keyEvent.scanCode} unicodeChar ${keyEvent.unicodeChar} source: ${keyEvent.source} repeatcount: ${keyEvent.repeatCount}"
        )

        //if the button is pressed continuously then it creates multiple repeat count
        //this will prevent the user from holding the key for long duration (long key press leads to multiple repeat count)
        if(keyEvent.repeatCount>0){
            // Ignore repeated key presses
            return true
        }

        if (keyEvent.source == InputDevice.SOURCE_HDMI)
            return true

        return when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_STB -> onSTBKeyDown(keyCode, keyEvent)
            Constants.BUILD_TYPE_CHROMECAST -> onCCKeyDown(keyCode, keyEvent)
            else -> false
        }

    }

    private fun onCCKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        Log.e(TAG, "onCCKeyDown: ")
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBackKeyAndExitKey()
        }

        when (keyEvent.scanCode) {
            Constants.APP_WORLD_KEY -> {
                if (!checkMenuButtonInButtonListExists(Constants.APPS_ID)) {
                    //return it: don't do anything
                    return true
                }
                ThemeDetails.TITLE = Constants.APPS
                if (currentActivity !is AppWorldActivity) {
                    intent = Intent(this, AppWorldActivity::class.java)
                    startActivity(intent)
                }
            }

            Constants.LIVE_TV_KEY,
            Constants.GUIDE_KEY -> {
                if (!checkMenuButtonInButtonListExists(Constants.PRG_GUIDE_ID)) {
                    //return it: don't do anything
                    return true
                }

                if (currentActivity !is NewProgramGuideActivity) {
                    ThemeDetails.TITLE = Constants.PROGRAM_GUIDE
                    val intent = Intent(this, NewProgramGuideActivity::class.java)
                    startActivity(intent)
                }
            }

            Constants.CASTING_KEY -> {
                if (!checkMenuButtonInButtonListExists(Constants.CASTING_ID)) {
                    //return it: don't do anything
                    return true
                }
                if (currentActivity !is CastingActivity) {
                    if (castingUrl.isNotEmptyOrNull()) {
                        intent = Intent(this, CastingActivity::class.java)
                    } else {
                        showToast(getString(R.string.please_contact_the_front_desk_for_assistance))
                    }
                    startActivity(intent)
                }
            }

            Constants.EXIT_KEY -> handleBackKeyAndExitKey()

            Constants.PROGRAM_SEARCH_KEY -> {
                if (currentActivity is NewProgramGuideActivity) {
                    ThemeDetails.TITLE = Constants.PROGRAM_GUIDE
                    (currentActivity as NewProgramGuideActivity).showSearchDialog()
                }
            }

            Constants.NETFLIX_KEY -> onBaseAppClicked(Constants.NETFLIX_PACKAGE_NAME)
            Constants.YOUTUBE_KEY -> onBaseAppClicked(Constants.YOUTUBE_PACKAGE_NAME)

        }

        return false
    }

    private fun onSTBKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        Log.e(TAG, "onSTBKeyDown: $keyCode")
        when (keyCode) {
            KeyEvent.KEYCODE_BACK,
            Constants.ATV_EXIT_KEYCODE -> handleBackKeyAndExitKey()

            Constants.ATV_APPS_KEYCODE -> {
                if (!checkMenuButtonInButtonListExists(Constants.APPS_ID)) {
                    //return it: don't do anything
                    return true
                }
                ThemeDetails.TITLE = Constants.APPS
                if (currentActivity !is AppWorldActivity) {
                    intent = Intent(this, AppWorldActivity::class.java)
                    startActivity(intent)
                }
            }

            Constants.ATV_LIVE_TV_KEYCODE,
            Constants.ATV_GUIDE_KEYCODE -> {
                if (!checkMenuButtonInButtonListExists(Constants.PRG_GUIDE_ID)) {
                    return true
                }

                if (currentActivity !is NewProgramGuideActivity) {
                    ThemeDetails.TITLE = Constants.PROGRAM_GUIDE
                    val intent = Intent(this, NewProgramGuideActivity::class.java)
                    startActivity(intent)
                }
            }

            Constants.ATV_CASTING_KEYCODE -> {
                if (!checkMenuButtonInButtonListExists(Constants.CASTING_ID)) {
                    //return it: don't do anything
                    return true
                }
                if (currentActivity !is CastingActivity) {
                    intent = if (castingUrl.isNotEmptyOrNull()) {
                        Intent(this, CastingActivity::class.java)
                    } else {
                        Intent(this, HotspotActivity::class.java)
                    }
                    startActivity(intent)
                }
            }

            Constants.ATV_SEARCH_KEYCODE -> {
                if (currentActivity is NewProgramGuideActivity) {
                    ThemeDetails.TITLE = Constants.PROGRAM_GUIDE
                    (currentActivity as NewProgramGuideActivity).showSearchDialog()
                }
            }

            Constants.ATV_NETFLIX_KEYCODE -> onBaseAppClicked(Constants.NETFLIX_PACKAGE_NAME)
            Constants.ATV_YOUTUBE_KEYCODE -> onBaseAppClicked(Constants.YOUTUBE_PACKAGE_NAME)

            Constants.ATV_LAST_CHANNEL_KEYCODE -> showToast("LAST KEY is clicked")
            Constants.ATV_CAPTIONS_KEYCODE -> showToast("CC KEY is clicked")
            Constants.ATV_BLUE_KEYCODE -> showToast("BLUE KEY is clicked")

        }

        return false
    }

    override fun onResume() {
        super.onResume()

        currentActivity = this
        setIPInfo()

        activityStack.add(this::class.java.simpleName)
        if (this::class.java.simpleName == MainMenuActivity::class.java.simpleName) {
            activityStack.clear()
            activityStack.add(MainMenuActivity::class.java.simpleName)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkMenuButtonInButtonListExists(buttonName: String): Boolean {
        if (mainMenuButtonList.isNullOrEmpty()) {
            return false
        }

        val btnList = mainMenuButtonList.filter { it.buttonName == buttonName }
        Log.e(TAG, "checkMenuButtonInButtonListExists: ${btnList.isNotEmpty()}")
        return btnList.isNotEmpty()
    }
    private fun onBaseAppClicked(packageName: String) {
        if (isPackageExists(packageName)) {
            if (packageManager.getLaunchIntentForPackage(packageName) == null) {
                launchAppSecured(packageName)
            } else {
                launchApp(packageName)
            }
        } else showToast(getString(R.string.app_not_available))
    }

    private fun launchApp(packageName: String) {
        startActivity(
            packageManager.getLaunchIntentForPackage(packageName)?.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        )
    }

    private fun launchAppSecured(packageName: String?) {
        val intent = Intent()
        intent.setPackage(packageName)
        val pm = packageManager
        val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
        Collections.sort(resolveInfoList, ResolveInfo.DisplayNameComparator(pm))
        if (resolveInfoList.isNotEmpty()) {
            val launchAble = resolveInfoList[0]
            val activity = launchAble.activityInfo
            val name = ComponentName(
                activity.applicationInfo.packageName,
                activity.name
            )
            val i = Intent(Intent.ACTION_VIEW)
            if (packageName != null) {
                if (!packageName.contains(".settings")) {
                    i.component = name
                } else {
                    i.action = Settings.ACTION_SETTINGS
                }
            }
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            startActivity(i)
        }
    }

    companion object {
        var currentActivity: Activity? = null
        val activityStack: MutableList<String?> = mutableListOf()
    }
}
