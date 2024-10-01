package com.diipl.moviebeam.ui.base

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
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
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.weather.WeatherActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.setIPInfo
import com.diipl.moviebeam.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    lateinit var context: Context
    private var castingUrl = ""

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = this
        context = this
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)

        launchLogger()
        initViewBinding()
        observeViewModel()
        this.initializeDatastoreParams()

        logD("${this::class.java.simpleName} started.")
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            castingUrl = getCastingUrl()
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        Log.d("TAG", "onKeyDown: keycode: $keyCode keyEvent.keyCode ${keyEvent.keyCode} keyEvent.action ${keyEvent.action} keyEvent.displayLabel ${keyEvent.displayLabel}  keyEvent.number ${keyEvent.number} keyEvent.scanCode ${keyEvent.scanCode} keyEvent.unicodeChar ${keyEvent.unicodeChar}")
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
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

            }
        }

        if(keyEvent.scanCode == Constants.APP_WORLD_KEY)
        {
            //Apps
            if (currentActivity !is AppWorldActivity) {
                intent = Intent(this, AppWorldActivity::class.java)
                startActivity(intent)
            }
        }
        if(keyEvent.scanCode == Constants.LIVE_TV_KEY || keyEvent.scanCode == Constants.GUIDE_KEY)
        {
            //program Guide
            if (currentActivity !is NewProgramGuideActivity) {
                val bundle = Bundle()
                bundle.putString("title", "Program Guide")
                val intent = Intent(this, NewProgramGuideActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
        if(keyEvent.scanCode == Constants.CASTING_KEY)
        {
            //Casting
            if (currentActivity !is CastingActivity) {
                if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_STB)) {
                    if (castingUrl.isNullOrEmpty()) {
                        intent = Intent(this, HotspotActivity::class.java)
                    } else {
                        intent = Intent(this, CastingActivity::class.java)
                    }
                } else {
                    if (!castingUrl.isNullOrEmpty()) {
                        intent = Intent(this, CastingActivity::class.java)
                    } else {
                        showToast(getString(R.string.please_contact_the_front_desk_for_assistance))
                    }
                }
                startActivity(intent)
            }
        }
        if(keyEvent.scanCode == Constants.EXIT_KEY)
        {
            //Exit
        }
        if(keyEvent.scanCode == Constants.PROGRAM_SEARCH_KEY)
        {
            //Search
            if (currentActivity is NewProgramGuideActivity) {
                (currentActivity as NewProgramGuideActivity).showSearchDialog()
            }
        }
        if (keyEvent.scanCode == Constants.NETFLIX_KEY) {
            // Netflix
            onBaseAppClicked(Constants.NETFLIX_PACKAGE_NAME)
        }
        if(keyEvent.scanCode == Constants.YOUTUBE_KEY){
            //Youtube
            onBaseAppClicked(Constants.YOUTUBE_PACKAGE_NAME)
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

    private suspend fun getCastingUrl(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.CASTING_URL_KEY,
            ""
        )
    }

    protected open fun fragmentTransaction(
        transactionType: Int,
        fragment: Fragment,
        container: Int,
        isAddToBackStack: Boolean,
        bundle: Bundle?
    ) {
        if (bundle != null) {
            fragment.arguments = bundle
        }

        val trans = supportFragmentManager.beginTransaction()
        when (transactionType) {
            ADD_FRAGMENT -> trans.add(container, fragment, fragment.javaClass.simpleName)
            REPLACE_FRAGMENT -> {
                trans.replace(container, fragment, fragment.javaClass.simpleName)
                if (isAddToBackStack) trans.addToBackStack(null)
            }
        }
        trans.commit()
    }

    class CustomThreadExecutor {

        private lateinit var scheduledExecutorService: ScheduledExecutorService
        private lateinit var scheduledFuture: ScheduledFuture<*>
        var counter = 0

        init {
            //Start Scheduler as required
            startScheduler()
        }

        fun startScheduler() {
            scheduledExecutorService = Executors.newScheduledThreadPool(2)

            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(
                { tempImageFetch() }, 0, 60, TimeUnit.SECONDS
            )
        }

        fun shutdownScheduler() {
            //Stop before exit the app or when necessary
            scheduledExecutorService.shutdownNow()

        }

        private fun tempImageFetch() {
            //TODO call API
            counter++
            Log.d("counter", counter.toString())
        }
    }

    fun onBaseAppClicked(packageName: String) {
        if (context.packageManager.getLaunchIntentForPackage(packageName) == null) {
            launchAppSecured(packageName)
        } else {
            launchApp(packageName)
        }
    }

    private fun launchApp(packageName: String) {
        context.startActivity(
            context.packageManager.getLaunchIntentForPackage(packageName)?.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        )
    }

    private fun launchAppSecured(packageName: String?) {
        val intent = Intent()
        intent.setPackage(packageName)
        val pm = context.packageManager
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
            context.startActivity(i)
        }
    }

    companion object {
        const val ADD_FRAGMENT = 0
        const val REPLACE_FRAGMENT = 1
        var currentActivity: Activity? = null
        val activityStack: MutableList<String?> = mutableListOf()
    }
}
