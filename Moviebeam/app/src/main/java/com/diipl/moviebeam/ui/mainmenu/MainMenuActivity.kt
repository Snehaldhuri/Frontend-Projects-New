package com.diipl.moviebeam.ui.mainmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import com.diipl.moviebeam.ui.appworld.AppWorldActivity
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.casting.CastingActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity
import com.diipl.moviebeam.ui.inroomdining.InRoomDiningActivity
import com.diipl.moviebeam.ui.kappingservice.Actions
import com.diipl.moviebeam.ui.kappingservice.EndlessService
import com.diipl.moviebeam.ui.kappingservice.ServiceState
import com.diipl.moviebeam.ui.kappingservice.getServiceState
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.programguide.DisconnectedPrgActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.ALL_SERVICES
import com.diipl.moviebeam.utils.Constants.HOTEL_VIDEO_LOOP_COUNT
import com.diipl.moviebeam.utils.Constants.HOTEL_VIDEO_URL
import com.diipl.moviebeam.utils.Constants.LA_ID
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.log
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


private const val TAG = "MainMenuActivity"

@AndroidEntryPoint
class MainMenuActivity : BaseActivity() {

    private val mainMenuViewModel: MainMenuViewModel by viewModels()
    private lateinit var binding: ActivityMainMenuBinding
    private var isServiceStarted = false
    private lateinit var player: ExoPlayer
    private var latestAccountSetupResponse: AccountSetupResponse? = null

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var tickerDatastore: DataStore<TickerResponse>

    @Inject
    lateinit var guestDetailsDatastore: DataStore<CmdDataDto>

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var preference: SharedPreference

    override fun observeViewModel() {
        observe(mainMenuViewModel.themeLiveData, ::handleThemeResponse)
        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(mainMenuViewModel.tickerLiveData, ::handleTickerResponse)
        observe(mainMenuViewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(mainMenuViewModel.guestDetailsLiveData, ::handleGuestDetailsResponse)

        observeSnackBarMessages(mainMenuViewModel.showSnackBar)
        observeToast(mainMenuViewModel.showToast)
    }


    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)

        // call below function to get data from datastore
        mainMenuViewModel.getThemeResponseData(themeDataStore)
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
        mainMenuViewModel.getTickerResponseData(tickerDatastore)

        mainMenuViewModel.validateSession(preferenceDataStoreHelper)

        // start the endless service
        if (!isServiceStarted) {
            actionOnService(Actions.START)
        }
        LoggingService.sendMessageToWebSocket("In MainMenu activity", getCurrentPanelNumber())

    }

    override fun onResume() {
        super.onResume()

        player = ExoPlayer.Builder(this).build()
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setMaxVideoSizeSd()
            .build()
        initializePlayer()

        lifecycleScope.launch {
            val ua = preferenceDataStoreHelper.getFirstPreference(
                PreferenceDataStoreConstants.SERIAL_NO,
                ""
            )
            Constants.SERIAL_NO = ua
            Constants.UA = "21$ua"
        }

        binding.rvMenuButton.setItemFocused()

    }

    override fun initViewBinding() {
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnDisconnected.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.handleFocusChange()
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_ENTER ,KeyEvent.KEYCODE_DPAD_CENTER -> {
                                disconnectedMode()
                                return@setOnKeyListener true
                            }

                        }
                    }
                    false
                }
            } else {
                binding.btnDisconnected.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player.release()
        HOTEL_VIDEO_LOOP_COUNT = 3
    }

    private fun initializePlayer() {
        if (HOTEL_VIDEO_URL.isNotEmpty()) {
            binding.videoView.toVisible()
            binding.videoView.player = player
            player.setMediaItem(MediaItem.fromUri(HOTEL_VIDEO_URL))
            player.repeatMode = Player.REPEAT_MODE_ALL
            player.playWhenReady = true
            player.addListener(playerListener)
            player.prepare()
            player.play()
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
            HOTEL_VIDEO_LOOP_COUNT--
        }
    }

    private fun releaseVideoPlayer() {
        binding.videoView.toInvisible()
        player.release()
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                try {
                    val response = status.data

//                    binding.rvMenuButton.setBackgroundColor(resources.getColor(R.color.menu_list_bg))
                    response?.themeLogoFileName?.let {
//                    getImageBitmap(it, Constants.HOTEL_LOGO)
                        binding.ivHotelLogo.loadImagesWithGlideExtLogo(it)
                    }
                    response?.gradientColor?.let {
                        Constants.GRADIENT_COLOR_START = it
                    }
                    response?.spotLightColor?.let {
                        Constants.GRADIENT_COLOR_END = it
                    }
                    Constants.GRADIENT = getGradientColor()
                    Constants.LOGO_IMAGE = response?.themeLogoFileName
                    Constants.BG_IMAGE = response?.themeBackgroundFileName
                    response?.themeBackgroundFileName?.let {
//                    getImageBitmap(it, Constants.BACKGROUND_IMAGE)
                        loadBg(it)
                    }
                    binding.pbLoader.toInvisible()
                } catch (e: Exception) {
                    LoggingService.sendMessageToWebSocket(
                        "handleThemeResponse Exception in MainMenu activity ${e.message}",
                        getCurrentPanelNumber()
                    )
                }
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { mainMenuViewModel.showToastMessage(it) }
            }
        }
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
                    LoggingService.sendMessageToWebSocket(
                        "handleThemeResponse Exception in MainMenu activity ${e.message}",
                        getCurrentPanelNumber()
                    )
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
                        latestAccountSetupResponse = response // Store the latest response data

                        Constants.ACCOUNT_ID = response.accountId
                        Constants.STB_ROOM_NO = response.roomNo

                        if(Constants.DISCONNECTED_MODE){
                            releaseVideoPlayer()
                            loadBg(Constants.BG_IMAGE)
                        }else{
                            if (response.contentDetailFlag) {
                                HOTEL_VIDEO_URL =
                                    response.httpStreamingHotelvideoUrl + response.hotelChannelList[0].fileName
                                initializePlayer()
                            }
                        }

                        binding.tvGreeting.text = response.hotelInfo

                        val btnListFromApi: List<String> = if (Constants.DISCONNECTED_MODE) {
                            response.buttonsList.filter { it.forDisconnectedMode }.map { it.buttonName }
                        } else {
                            response.buttonsList.map { it.buttonName }
                        }

                        val btnModelList: List<BtnModel> = Constants.HOME_PAGE_MENU_BUTTON_LIST.filter {
                            btnListFromApi.contains(it.btnId)
                        }

                        val sortedBtnModelList: List<BtnModel> = btnModelList.sortedBy {
                            btnListFromApi.indexOf(it.btnId) ?: Int.MAX_VALUE
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
                            bundle.putString(
                                "hotelChannel",
                                response.hotelChannelList.get(0).toJson()
                            )
                            bundle.putString("title", btn.title)
                            Constants.TITLE = btn.title
                            bundle.putString(
                                "hotelChannel",
                                response.hotelChannelList.get(0).toJson()
                            )
                            val hotelChannelVideo =
                                response.httpStreamingHotelvideoUrl + response.hotelChannelList.get(
                                    0
                                ).fileName
                            bundle.putString("hotelChannelVideo", hotelChannelVideo)
                            bundle.putString(
                                "themeLogoFileName",
                                mainMenuViewModel.themeLiveData.value?.data?.themeLogoFileName
                            )
                            bundle.putString(
                                "themeBackgroundFileName",
                                mainMenuViewModel.themeLiveData.value?.data?.themeBackgroundFileName
                            )
                            bundle.putString(
                                "gradientStartColor",
                                Constants.GRADIENT_COLOR_START
                            )
                            bundle.putString(
                                "gradientEndColor",
                                Constants.GRADIENT_COLOR_END
                            )
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
                                    if(Constants.DISCONNECTED_MODE){
                                        intent = Intent(this, DisconnectedPrgActivity::class.java)
                                    }else {
                                        intent = Intent(this, ProgramGuideActivity::class.java)
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
                    LoggingService.sendMessageToWebSocket(
                        "handleAccountSetupResponse Exception in MainMenu activity ${e.message}",
                        getCurrentPanelNumber()
                    )
                }
            }
            else -> {
                status?.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun disconnectedMode() {
        if(Constants.DISCONNECTED_MODE){
            Constants.DISCONNECTED_MODE = false
            binding.btnDisconnected.text="Disconnected Mode"
        }
        else{
            Constants.DISCONNECTED_MODE = true
            binding.btnDisconnected.text="Connected Mode"
        }
        latestAccountSetupResponse?.let { response ->
            handleAccountSetupResponse(Resource.Success(response))
        }
    }

    private fun handleValidateSessionResponse(status: Boolean) {
        try {
            if (status) {
                mainMenuViewModel.getGuestDetails(guestDetailsDatastore)
                Constants.IS_CHECKED_IN = true
            }else
                Constants.IS_CHECKED_IN = false
            Constants.SESSION_ID = "null"
            binding.pbLoader.toInvisible()
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "handleValidateSessionResponse Exception in MainMenu activity ${e.message}",
                getCurrentPanelNumber()
            )
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
                            Constants.SESSION_ID = it.sessionId.toString()
                            binding.tvWelcome.text =
                                "Welcome ${it.guestFirstName} ${it.guestLastName}"
                            binding.tvWelcome.toVisible()
                            binding.pbLoader.toInvisible()
                        }
                    }
                } catch (e: Exception) {
                    LoggingService.sendMessageToWebSocket(
                        "handleGuestDetailsResponse Exception in MainMenu activity ${e.message}",
                        getCurrentPanelNumber()
                    )
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

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl).into(object : CustomTarget<Drawable?>() {
            override fun onResourceReady(
                resource: Drawable, transition: Transition<in Drawable?>?
            ) {
                binding.root.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
    }

    private fun getImageBitmap(imageUrl: String, filename: String) {
        Glide.with(this).asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // The 'resource' parameter contains the Bitmap loaded from the imageUrl
                // Now you can use the bitmap as needed, for example, save it locally
                saveImageLocally(resource, filename)

            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }

    // Save the image locally
    fun saveImageLocally(bitmap: Bitmap, filename: String) {
        val directory = File(getExternalFilesDir(null), Constants.THEME_DIRECTORY)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, filename)

        try {
            if (!file.exists()) {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
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
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {}
        }
        return false
    }

}
