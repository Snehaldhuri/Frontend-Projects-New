package com.diipl.moviebeam.ui.mainmenu

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.updateLayoutParams
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.Buttons
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.ticker.TickerResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.service.handler.ClearCredentialsHandler
import com.diipl.moviebeam.service.kappingservice.Actions
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.service.kappingservice.ServiceState
import com.diipl.moviebeam.service.kappingservice.getServiceState
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.casting.CastingActivity
import com.diipl.moviebeam.ui.casting.HotspotActivity
import com.diipl.moviebeam.ui.concierge.ConciergeActivity
import com.diipl.moviebeam.ui.guest.feedback.GuestFeedbackActivity
import com.diipl.moviebeam.ui.guest.message.GuestMessageActivity
import com.diipl.moviebeam.ui.guest.news.NewsActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceViewModel
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.inroomdining.InRoomDiningActivity
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.newprogramguide.NewProgramGuideActivity
import com.diipl.moviebeam.ui.programguide.DisconnectedPrgActivity
import com.diipl.moviebeam.ui.refreshingui.RefreshingUiViewModel
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.ui.weather.WeatherActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.ALL_SERVICES
import com.diipl.moviebeam.utils.Constants.LA_ID
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.animateScale
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.openSettingsPattern
import com.diipl.moviebeam.utils.setItemFocused
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

private const val TAG = "MainMenuActivity"

@AndroidEntryPoint
class MainMenuActivity : BaseActivity() {

    private val mainMenuViewModel: MainMenuViewModel by viewModels()

    private val guestServiceViewModel: GuestServiceViewModel by viewModels()

    private val refreshingUiViewModel: RefreshingUiViewModel by viewModels()

    @Inject
    lateinit var guestMessageDataStore: DataStore<MessageResponse>

    private lateinit var binding: ActivityMainMenuBinding

    private var isServiceStarted = false
    private lateinit var player: ExoPlayer
    private var isNetworkConnected = 0
    private var playCount = 0

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

    private val clearCredentialsHandler: ClearCredentialsHandler by lazy {
        ClearCredentialsHandler(
            applicationContext,
            accountSetupDataStore
        )
    }

    override fun observeViewModel() {
        observe(mainMenuViewModel.networkStatus, ::handleNetworkResponse)
        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(mainMenuViewModel.tickerLiveData, ::handleTickerResponse)
        observe(mainMenuViewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(mainMenuViewModel.guestDetailsLiveData, ::handleGuestDetailsResponse)
        observe(guestServiceViewModel.guestMessageLiveData, ::handleGuestMessageResponse)

        observeSnackBarMessages(mainMenuViewModel.showSnackBar)
        observeToast(mainMenuViewModel.showToast)

    }

    private fun handleNetworkResponse(isConnected: Boolean) {
        if (isConnected) {
            isNetworkConnected = 1
            if (preferenceHandler.isContentDetailFlagEnabled)
                binding.videoView.toVisible()
        } else {
            isNetworkConnected = -1
            releaseVideoPlayer()
        }
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        guestServiceViewModel.getGuestMessageResponseData(guestMessageDataStore)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)

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
            .setMaxVideoSizeSd()
            .build()
        binding.videoView.player = player
    }

    override fun onResume() {
        super.onResume()

        logD("isContentDetailFlagEnabled: ${preferenceHandler.isContentDetailFlagEnabled}, hotelVideoUrl: ${preferenceHandler.hotelVideoUrl}")

        if (preferenceHandler.isContentDetailFlagEnabled) initializePlayer()
        else {
            releaseVideoPlayer()
        }

        binding.cardView.postDelayed({
            binding.cardView.toVisible()
        }, 240)

        lifecycleScope.launch {
            while (isActive) {
                binding.ivHotelLogo.loadLogo()
                binding.root.loadBg()
                if (ThemeDetails.LOGO_IMAGE != null && ThemeDetails.BG_IMAGE != null)
                    this.cancel()
                delay(500)
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
        releaseVideoPlayer()
        HOTEL_VIDEO_LOOP_COUNT = 3
    }

    private fun initializePlayer() {
        init()
        playCount++
        if (preferenceHandler.hotelVideoUrl.isNotEmpty()) {
            binding.videoView.toVisible()
            player.setMediaItem(MediaItem.fromUri(preferenceHandler.hotelVideoUrl))
            player.repeatMode = Player.REPEAT_MODE_ALL
            player.addListener(playerListener)
            player.playWhenReady = true
            player.prepare()
            player.play()
        } else {
            if (playCount <= 2) {
                lifecycleScope.launch {
                    delay(2000)
                    initializePlayer()
                }
            } else releaseVideoPlayer()
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            logE("onPlayerError: ${error.localizedMessage}")
            if (error.localizedMessage!! == "Source error") releaseVideoPlayer()
            if (error.localizedMessage!!.startsWith("MediaCodecAudioRenderer error")) releaseVideoPlayer()
            if (error.localizedMessage!!.startsWith("Unexpected runtime error")) releaseVideoPlayer()
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (HOTEL_VIDEO_LOOP_COUNT == 0) {
                releaseVideoPlayer()
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (reason == 0) HOTEL_VIDEO_LOOP_COUNT -= 1
        }
    }

    private fun releaseVideoPlayer() {
        binding.videoView.toGone()
        binding.root.loadBg()
        try {
            if (::player.isInitialized) {
                player.stop()
                player.release()
            }
        } catch (e: Exception){
            Log.e(TAG, "releaseVideoPlayer: ${e.localizedMessage}")
        }
    }

    private fun handleTickerResponse(status: Resource<TickerResponse>) {
        when (status) {
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
            is Resource.Success -> {
                try {
                    status.data?.let { response ->

                        val guestMessages: Int =
                            guestServiceViewModel.guestMessageLiveData.value?.data?.messagesList?.size
                                ?: 0

                        val containsMainMsg =
                            response.buttonsList.find { button -> button.buttonName == "mainmsg" } != null
                        refreshingUiViewModel.setMainMsgStatus(containsMainMsg)

                        binding.tvGreeting.text = response.hotelInfo

                        if (response.isEnablePatchWall)
                            showPatchWall()
                        if (response.enableClearCredentialsPatchwall)
                            showClearCredentialsPatchWall()

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

                        val btnModelList: MutableList<BtnModel> =
                            Constants.HOME_PAGE_MENU_BUTTON_LIST.filter {
                                btnListFromApi.contains(it.btnId)
                            }.toMutableList()

                        if (guestMessages == 0) {
                            btnModelList.remove(Constants.MENU_MESSAGE_MODEL)
                        }
                        val sortedBtnModelList =
                            response.buttonsList.let { matchAndSortButtons(it) }

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
                                    if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_STB)) {
                                        if (preferenceHandler.castingUrl.isNullOrEmpty()) {
                                            intent = Intent(this, HotspotActivity::class.java)
                                        } else {
                                            intent = Intent(this, CastingActivity::class.java)
                                        }
                                    } else {
                                        if (!preferenceHandler.castingUrl.isNullOrEmpty()) {
                                            intent = Intent(this, CastingActivity::class.java)
                                        } else {
                                            showToast(getString(R.string.please_contact_the_front_desk_for_assistance))
                                        }
                                    }
                                }

                                Constants.PRG_GUIDE_ID -> {
                                    intent = if (isNetworkConnected == -1) {
                                        Intent(this, DisconnectedPrgActivity::class.java)
                                    } else {
                                        Intent(this, NewProgramGuideActivity::class.java)
                                    }

                                }

                                Constants.IN_ROOM_DINING_ID -> {
                                    intent = Intent(this, InRoomDiningActivity::class.java)
                                }

                                Constants.CONCIERGE_MAIN_ID -> {
                                    intent = Intent(this, ConciergeActivity::class.java)
                                }

                                Constants.MAIN_WEATHER_ID -> {
                                    intent = Intent(this, WeatherActivity::class.java)
                                }

                                Constants.MAIN_GUEST_MSG_ID -> {
                                    intent = Intent(this, GuestMessageActivity::class.java)
                                }

                                Constants.MAIN_FEEDBACK_ID -> {
                                    intent = Intent(this, GuestFeedbackActivity::class.java)
                                }

                                Constants.MAIN_NEWS_ID -> {
                                    intent = Intent(this, NewsActivity::class.java)
                                }

                                else -> {

                                }
                            }
                            intent?.let {
                                it.putExtras(bundle)
                                startActivity(it)
                            }
                        }
                        if (sortedBtnModelList != null) {
                            adapter.itemList = sortedBtnModelList
                            if (sortedBtnModelList.size < 5) {
                                binding.cardView.layoutParams.height = 220
                            }
                        }
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

    private fun matchAndSortButtons(apiButtons: List<Buttons>): List<BtnModel> {
        val btnModelList = mutableListOf<BtnModel>()

        apiButtons.forEach { apiButton ->
            Constants.HOME_PAGE_MENU_BUTTON_LIST.find {
                it.btnId.equals(
                    apiButton.buttonName,
                    ignoreCase = true
                )
            }?.let { matchedBtn ->
                btnModelList.add(matchedBtn)
            }

            if (apiButton.isApp == true)
                btnModelList.add(BtnModel(isApp = true, appPackageId = apiButton.appPackageId!!))
        }

        return btnModelList
    }

    private fun handleGuestMessageResponse(status: Resource<MessageResponse>) {
        when (status) {
            is Resource.Success -> {
                try {
                    guestServiceViewModel.getAccountSetupResponseData(accountSetupDataStore)
//                    binding.loaderView.toInvisible()
                } catch (e: Exception) {
                    logE("handleGuestMessageResponse Exception in GuestServiceActivity: ${e.message}")
                }
            }

            else -> {
                status.errorCode?.let { guestServiceViewModel.showToastMessage(getString(it)) }
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

    private fun showPatchWall() = lifecycleScope.launch {
        binding.netflixApp.setImageDrawable(packageManager.getApplicationBanner(Constants.NETFLIX_PACKAGE_NAME))
        binding.primeVideoApp.setImageDrawable(packageManager.getApplicationBanner(Constants.PRIME_VIDEO_PACKAGE_NAME))

        binding.netflixApp.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.cardClearCredentials.animateScale()
               /* view?.setOnKeyListener { _, keycode, keyEvent ->
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
                }*/
            } else {
                binding.cardClearCredentials.animateScale(false)
            }
        }
        binding.netflixApp.setOnClickListener { handleClick(Constants.NETFLIX_PACKAGE_NAME) }
        binding.primeVideoApp.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.cardClearCredentials.animateScale()
               /* view?.setOnKeyListener { _, keycode, keyEvent ->
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
                }*/
            } else {
                binding.cardClearCredentials.animateScale(false)
            }
        }
        binding.primeVideoApp.setOnClickListener { handleClick(Constants.PRIME_VIDEO_PACKAGE_NAME) }

        binding.netflixApp.openSettingsPattern()
        binding.primeVideoApp.openSettingsPattern()

        delay(500)
        binding.rvMenuButton.setItemFocused()
        binding.panelView.toVisible()
    }

    private fun showClearCredentialsPatchWall() = lifecycleScope.launch {
        binding.cardClearCredentials.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.cardClearCredentials.strokeWidth = 0
                binding.tvClearCredentials.background = getGradientColor()
                binding.cardClearCredentials.animateScale()
               /* view?.setOnKeyListener { _, keycode, keyEvent ->
                    if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keycode) {
                            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                binding.rvMenuButton.requestFocus()
                                true
                            }

                            else -> true
                        }
                    } else {
                        false
                    }
                }*/
            } else {
                binding.cardClearCredentials.strokeWidth = 0
                binding.tvClearCredentials.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                binding.cardClearCredentials.animateScale(false)
            }
        }
        binding.cardClearCredentials.setOnClickListener {
            clearCredentialsHandler.startClearCredentials()
        }
        binding.cardClearCredentials.openSettingsPattern()

        delay(500)
        binding.rvMenuButton.setItemFocused()
        binding.clearCredentialsPanelView.toVisible()
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
