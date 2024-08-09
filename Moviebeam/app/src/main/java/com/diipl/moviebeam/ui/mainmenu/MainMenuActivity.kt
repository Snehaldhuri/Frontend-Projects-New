package com.diipl.moviebeam.ui.mainmenu

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.view.updateLayoutParams
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.GridLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.service.kappingservice.Actions
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.service.kappingservice.ServiceState
import com.diipl.moviebeam.service.kappingservice.getServiceState
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.casting.CastingActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.inroomdining.InRoomDiningActivity
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.newprogramguide.NewProgramGuideActivity
import com.diipl.moviebeam.ui.programguide.DisconnectedPrgActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.ALL_SERVICES
import com.diipl.moviebeam.utils.Constants.LA_ID
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setItemFocused
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@AndroidEntryPoint
class MainMenuActivity : BaseActivity() {

    private val mainMenuViewModel: MainMenuViewModel by viewModels()
    private lateinit var binding: ActivityMainMenuBinding

    //Variables from datastore
    private var hotelVideoUrl = ""
    private var gradientStartColor = ""
    private var gradientEndColor = ""

    private var isServiceStarted = false
    private lateinit var player: ExoPlayer
    private var isNetworkConnected = 0

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var guestDetailsDatastore: DataStore<CmdDataDto>

    @Inject
    lateinit var tickerDatastore: DataStore<TickerResponse>

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var preference: SharedPreference

    override fun observeViewModel() {
        observe(mainMenuViewModel.networkStatus, ::handleNetworkResponse)
        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(mainMenuViewModel.tickerLiveData, ::handleTickerResponse)
        observe(mainMenuViewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(mainMenuViewModel.guestDetailsLiveData, ::handleGuestDetailsResponse)

        observeSnackBarMessages(mainMenuViewModel.showSnackBar)
        observeToast(mainMenuViewModel.showToast)

    }

    private fun handleNetworkResponse(isConnected: Boolean) {
        if (isConnected) {
            isNetworkConnected = 1
            binding.videoView.toVisible()
        } else {
            isNetworkConnected = -1
            releaseVideoPlayer()
            binding.root.post {
                binding.root.loadBg()
            }
        }
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        this.initializeDatastoreParams()

        // call below function to get data from datastore
        mainMenuViewModel.getNetworkStatus(preferenceDataStoreHelper)

        mainMenuViewModel.getThemeResponseData(themeDataStore)
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
        mainMenuViewModel.getTickerResponseData(tickerDatastore)

        mainMenuViewModel.validateSession(preferenceDataStoreHelper)

        // start the endless service
        if (!isServiceStarted) {
            actionOnService(Actions.START)
        }

    }

    private fun init() {
        player = ExoPlayer.Builder(this).build()
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setMaxVideoSize(1920, 1080)
            .build()
        binding.videoView.player = player
    }

    override fun onResume() {
        super.onResume()

        initializePlayer()
        binding.root.loadBg()
        binding.rvMenuButton.setItemFocused()

        binding.cardView.postDelayed({
            binding.cardView.toVisible()
        }, 500)

        lifecycleScope.launch {
            while (!player.isPlaying) {
                if (hotelVideoUrl.isNotEmpty() && HOTEL_VIDEO_LOOP_COUNT > 0) {
                    initializePlayer()
                    binding.videoView.toGone()
                }
                delay(5000)
            }
        }

    }

    override fun initViewBinding() {
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        binding.ivHotelLogo.loadLogo()
        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
        player.stop()
        player.release()
        HOTEL_VIDEO_LOOP_COUNT = 3
    }

    private fun animateScale(view: View, animationId: Int) {
        val anim: Animation = AnimationUtils.loadAnimation(view.context, animationId)
        view.startAnimation(anim)
        anim.fillAfter = true
    }

    private fun initializePlayer() {

        if (!::player.isInitialized) {
            init()
        }

        if (hotelVideoUrl.isNotEmpty()) {
            binding.videoView.toVisible()
            player.setMediaItem(MediaItem.fromUri(hotelVideoUrl))
            player.repeatMode = Player.REPEAT_MODE_ALL
            player.addListener(playerListener)
            player.playWhenReady = true
            player.prepare()
        } else {
            lifecycleScope.launch {
                delay(2000)
                initializePlayer()
            }
        }
    }


    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            releaseVideoPlayer()
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (HOTEL_VIDEO_LOOP_COUNT == 0) {
                releaseVideoPlayer()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            logD("onMediaItemTransition: $reason")
            if (reason == 0) HOTEL_VIDEO_LOOP_COUNT--
        }
    }

    private fun releaseVideoPlayer() {
        binding.videoView.toGone()
        if (::player.isInitialized)
            player.release()
    }

    private fun handleTickerResponse(status: Resource<TickerResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                try {
                    status.data?.let { response ->
                        if (!response.tvTickerList.isNullOrEmpty()) {
                            val message = StringBuilder()
                            response.tvTickerList?.forEach {
                                message.append(it.msg).append(" ")
                            }
                            binding.tvTickerMessage.text = message.toString()
                            binding.tvTickerMessage.isSelected = true
                        } else {
                            binding.tvTickerMessage.text = ""
                            binding.tvTickerMessage.isSelected = false
                        }
                    }
                } catch (e: Exception) {
                    logE("handleThemeResponse Exception in MainMenu activity ${e.message}")
                }
                binding.pbLoader.toInvisible()

            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { mainMenuViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>?) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                try {
                    status.data?.let { response ->
                        binding.tvGreeting.text = response.hotelInfo
                        if (response.isEnablePatchWall)
                            showPatchWall()


                        var btnListFromApi = listOf<String>()
                        when (isNetworkConnected) {
                            1 -> {
                                btnListFromApi = response.buttonsList.map { it.buttonName }
                            }

                            -1 -> {
                                btnListFromApi =
                                    response.buttonsList.filter { it.forDisconnectedMode }
                                        .map { it.buttonName }
                            }
                        }

                        val btnModelList: List<BtnModel> =
                            Constants.HOME_PAGE_MENU_BUTTON_LIST.filter {
                                btnListFromApi.contains(it.btnId)
                            }

                        val sortedBtnModelList: List<BtnModel> = btnModelList.sortedBy {
                            btnListFromApi.indexOf(it.btnId)
                        }
                        val height = if (sortedBtnModelList.size < 5) {
                            resources.getDimensionPixelSize(R.dimen.dp_110)
                        } else {
                            resources.getDimensionPixelSize(R.dimen.dp_200)
                        }

                        val marginTop = if (sortedBtnModelList.size < 5) {
                            resources.getDimensionPixelSize(R.dimen.dp_70)
                        } else {
                            resources.getDimensionPixelSize(R.dimen.dp_1)
                        }

                        binding.cardView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            this.height = height
                            this.topMargin = marginTop
                        }

                        binding.rvMenuButton.layoutManager = GridLayoutManager(this, 4)

                        val adapter = MainMenuBtnAdapter { btn ->
                            releaseVideoPlayer()
                            val bundle = Bundle()
                            ThemeDetails.TITLE = btn.title
                            bundle.putString(
                                "hotelChannel",
                                response.hotelChannelList.get(0).toJson()
                            )
                            val hotelChannelVideo =
                                response.httpStreamingHotelvideoUrl + response.hotelChannelList.get(
                                    0
                                ).fileName
                            bundle.putString("hotelChannelVideo", hotelChannelVideo)
                            var intent: Intent? = null
                            when (btn.btnId) {
                                Constants.HOTEL_SERVICES_ID -> {
                                    intent = Intent(this, HotelInfoActivity::class.java)
                                }

                                Constants.LOCAL_ATTRACTION_ID -> {
                                    intent = Intent(this, GuestServiceActivity::class.java)
                                    intent.putExtra("btnId", LA_ID)
                                }

                                Constants.VOD_ID -> {
                                    intent = Intent(this, MoviesActivity::class.java)
                                }

                                Constants.GUEST_SERVICES_ID -> {
                                    intent = Intent(this, GuestServiceActivity::class.java)
                                    intent.putExtra("btnId", ALL_SERVICES)
                                }

                                Constants.APPS_ID -> {
                                    intent = Intent(this, AppWorldActivity::class.java)
                                }

                                Constants.SHOWTIMES_ID -> {
                                    intent = Intent(this, ShowtimeActivity::class.java)
                                }

                                Constants.CASTING_ID -> {
                                    intent = Intent(this, CastingActivity::class.java)
                                }

                                Constants.PRG_GUIDE_ID -> {
                                    intent = if (isNetworkConnected == -1) {
                                        Intent(this, DisconnectedPrgActivity::class.java)
                                    } else {
                                        //Intent(this, ProgramGuideActivity::class.java)
                                        Intent(this, NewProgramGuideActivity::class.java)
                                    }
                                }

                                Constants.IN_ROOM_DINING_ID -> {
                                    intent = Intent(this, InRoomDiningActivity::class.java)
//                            intent = Intent(this, GuestServiceActivity::class.java)
//                            intent.putExtra("btnId", IN_ROOM_ID)
                                }

                                else -> {

                                }
                            }
                            intent?.let {
                                it.putExtras(bundle)
                                startActivity(it)
                            }
                        }
                        adapter.itemList = sortedBtnModelList
                        binding.rvMenuButton.adapter = adapter

                        binding.pbLoader.toInvisible()
                    }
                } catch (e: Exception) {
                    logE("handleAccountSetupResponse Exception in MainMenu activity ${e.message}")
                }
            }

            else -> {
                status?.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleValidateSessionResponse(status: Boolean) {
        try {
            if (status) {
                mainMenuViewModel.getGuestDetails(guestDetailsDatastore)
                GuestDetails.IS_GUEST_CHECKED_IN = true
            } else
                GuestDetails.IS_GUEST_CHECKED_IN = false
            GuestDetails.SESSION_ID = "null"
            binding.pbLoader.toInvisible()
        } catch (e: Exception) {
            logE("handleValidateSessionResponse Exception in MainMenu activity ${e.message}")
        }
    }

    private fun handleGuestDetailsResponse(status: Resource<CmdDataDto>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                try {
                    status.data?.let {
                        if (it.guestFirstName.isNullOrEmpty()) {
                            binding.tvWelcome.toGone()
                            binding.pbLoader.toGone()
                        } else {
                            GuestDetails.SESSION_ID = it.sessionId.toString()
                            binding.tvWelcome.text =
                                "Welcome ${it.guestFirstName} ${it.guestLastName}"
                            binding.tvWelcome.toVisible()
                            binding.pbLoader.toInvisible()
                        }
                    }
                } catch (e: Exception) {
                    logE("handleGuestDetailsResponse Exception in MainMenu activity ${e.message}")
                }
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun actionOnService(action: Actions) {
        if (action == Actions.STOP) {
            isServiceStarted = false
        } else if (action == Actions.START) {
            isServiceStarted = true
        }
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                logD("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            logD("Starting the service in < 26 Mode")
            startService(it)
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {}
        }
        return false
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            hotelVideoUrl = getHotelVideoUrl()
            gradientStartColor = getGradientStartColor()
            gradientEndColor = getGradientEndColor()
        }
    }

    private suspend fun getHotelVideoUrl(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.HOTEL_VIDEO_URL_KEY,
            ""
        )
    }

    private suspend fun getGradientStartColor(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
            Constants.DEFAULTGRADIENTSTARTCOLOR
        )
    }

    private suspend fun getGradientEndColor(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
            Constants.DEFAULTGRADIENTENDCOLOR
        )
    }

    private fun showPatchWall() {
        binding.panelView.toVisible()
        binding.netflixApp.setImageDrawable(packageManager.getApplicationBanner(Constants.NETFLIX_PACKAGE_NAME))
        binding.primeVideoApp.setImageDrawable(packageManager.getApplicationBanner(Constants.PRIME_VIDEO_PACKAGE_NAME))

        binding.netflixApp.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                animateScale(binding.netflixCardApp, R.anim.scale_in_animation)
                view?.setOnKeyListener { _, keycode, keyEvent ->
                    if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keycode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                binding.rvMenuButton.requestFocus()
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT -> true

                            else -> false
                        }
                    } else {
                        false
                    }
                }
            } else {
                animateScale(binding.netflixCardApp, R.anim.scale_out_animation)
            }
        }
        binding.netflixApp.setOnClickListener { handleClick(Constants.NETFLIX_PACKAGE_NAME) }
        binding.primeVideoApp.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                animateScale(binding.primeVideoCardApp, R.anim.scale_in_animation)
                view?.setOnKeyListener { _, keycode, keyEvent ->
                    if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keycode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                binding.rvMenuButton.requestFocus()
                                true
                            }

                            else -> false
                        }
                    } else {
                        false
                    }
                }
            } else {
                animateScale(binding.primeVideoCardApp, R.anim.scale_out_animation)
            }
        }
        binding.primeVideoApp.setOnClickListener { handleClick(Constants.PRIME_VIDEO_PACKAGE_NAME) }
        binding.rvMenuButton.post { binding.rvMenuButton.requestFocus() }
    }

    private fun handleClick(packageName: String) {
        if (GuestDetails.IS_GUEST_CHECKED_IN) {
            launchApplication(packageName)
        } else {
            showToast(getString(R.string.please_contact_the_front_desk_for_assistance))
        }
    }

    private fun launchApplication(packageName: String) {
        if (packageManager.getLaunchIntentForPackage(packageName) == null) {
            launchAppSecured(packageName)
        } else {
            launchApp(packageName)
        }
    }

    private fun launchApp(packageName: String) {
        try {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
        } catch (e: Exception) {
            e.printStackTrace()
            logE("launchApp Exception in Main Menu activity ${e.message}")
        }
    }

    private fun launchAppSecured(packageName: String?) {
        try {
            val intent = Intent()
            intent.setPackage(packageName)
            val pm = packageManager
            val resolveInfos = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            Collections.sort(resolveInfos, ResolveInfo.DisplayNameComparator(pm))
            if (resolveInfos.size > 0) {
                val launchAble = resolveInfos[0]
                val activity = launchAble.activityInfo
                val name = ComponentName(
                    activity.applicationInfo.packageName,
                    activity.name
                )
                val i = Intent(Intent.ACTION_MAIN)
                i.component = name
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                startActivity(i)
            }
        } catch (e: Exception) {
            logE("launchAppSecured Exception in Main Menu activity ${e.message}")
        }
    }

    companion object {
        var HOTEL_VIDEO_LOOP_COUNT = 3
    }

}
