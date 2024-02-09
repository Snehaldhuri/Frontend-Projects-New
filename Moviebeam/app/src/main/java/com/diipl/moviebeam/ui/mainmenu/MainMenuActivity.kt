package com.diipl.moviebeam.ui.mainmenu

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
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
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.Constants.ALL_SERVICES
import com.diipl.moviebeam.Constants.HOTEL_VIDEO_LOOP_COUNT
import com.diipl.moviebeam.Constants.HOTEL_VIDEO_URL
import com.diipl.moviebeam.Constants.LA_ID
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.data.kaping.CmdDataDto
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
import com.diipl.moviebeam.ui.movies.MoviesActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideActivity
import com.diipl.moviebeam.ui.showtime.ShowtimeActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.log
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

private const val TAG = "MainMenuActivity"

@AndroidEntryPoint
class MainMenuActivity : BaseActivity() {

    private val mainMenuViewModel: MainMenuViewModel by viewModels()
    private lateinit var binding: ActivityMainMenuBinding
    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR
    private var isServiceStarted = false
    private lateinit var player: ExoPlayer

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var guestDetailsDatastore: DataStore<CmdDataDto>

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    override fun observeViewModel() {
        observe(mainMenuViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(mainMenuViewModel.themeLiveData, ::handleThemeResponse)
        observe(mainMenuViewModel.accountSetupLiveData, ::handleAccountSetupResponse)

        observeSnackBarMessages(mainMenuViewModel.showSnackBar)
        observeToast(mainMenuViewModel.showToast)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)

        player = ExoPlayer.Builder(this).build()
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setMaxVideoSizeSd()
            .build()

        // call below function to get data from datastore
        mainMenuViewModel.getThemeResponseData(themeDataStore)
        mainMenuViewModel.getAccountSetupResponseData(accountSetupDataStore)
        mainMenuViewModel.getWeatherResponseData(weatherDataStore)
        mainMenuViewModel.validateSession(preferenceDataStoreHelper)

        val url = "https://tvbox-app.com/wp-content/uploads/2021/11/File-Manager_v2.6.5.apk"
//        startDownload(url)

        // UA = intent.extras?.getString("UA")

        // start the endless service
        if (!isServiceStarted) {
            actionOnService(Actions.START)
        }

    }

    fun disablePackage(packageName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val adbCommand = "adb shell pm disable-user --user 0 $packageName"
        observe(mainMenuViewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(mainMenuViewModel.guestDetailsLiveData, ::handleGuestDetailsResponse)

                Runtime.getRuntime().exec(adbCommand)

           /*     val processBuilder = ProcessBuilder("su", "-c", adbCommand)
                processBuilder.redirectErrorStream(true)

                val process = processBuilder.start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }

                process.waitFor()
                process.destroy()*/

                Log.e(TAG, "disablePackage: Disabled")
            } catch (e: Exception) {
//            e.printStackTrace()
                Log.e(TAG, "disablePackage: error ${e.message}")
            }

        }
    }

    fun startDownload(fileURL: String) = CoroutineScope(Dispatchers.Default).launch {
        try {
            val url = URL(fileURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val directory = File(externalMediaDirs[0].path + "/APK")
                if (!directory.exists()) directory.mkdirs()
                val file = File(directory, url.path.substringAfterLast("/"))
                val outputStream = FileOutputStream(file)
                val inputStream = connection.inputStream
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                inputStream.close()
                outputStream.close()
                Log.e(TAG, "startDownload: Completed  --->  ${file.absolutePath}")

            } else {
                // Handle the error or show a message if download fails
                Log.e(TAG, "startDownload: Failed")
            }
            connection.disconnect()

        } catch (e: Exception) {
            Log.e(TAG, "startDownload Exception: ${e.message}")
        }
    }

    override fun initViewBinding() {
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onPause() {
//        HOTEL_VIDEO_DURATION = player.currentPosition
        player.pause()
        player.release()
        HOTEL_VIDEO_LOOP_COUNT = 3
        super.onPause()
        Log.e(TAG, "onPause: ")
    }

    override fun onRestart() {
        super.onRestart()
        player = ExoPlayer.Builder(this).build()
        initializePlayer()
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
//            player.seekTo(HOTEL_VIDEO_DURATION)
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

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.tvTemperature.text =
                    mainMenuViewModel.weatherLiveData.value?.data?.tempCondition
                mainMenuViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.ivWeather.loadImagesWithGlideExt(it)
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { mainMenuViewModel.showToastMessage(it) }

            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                val response = mainMenuViewModel.themeLiveData.value?.data

                binding.rvMenuButton.setBackgroundColor(resources.getColor(R.color.menu_list_bg))
                response?.themeLogoFileName?.let {
//                    getImageBitmap(it, Constants.HOTEL_LOGO)
                    binding.ivHotelLogo.loadImagesWithGlideExtLogo(it)
                }
                response?.gradientColor?.let {
                    gradientStartColor = it
                }
                response?.spotLightColor?.let {
                    gradientEndColor = it
                }
                response?.themeBackgroundFileName?.let {
//                    getImageBitmap(it, Constants.BACKGROUND_IMAGE)
                    loadBg(it)
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { mainMenuViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val response = mainMenuViewModel.accountSetupLiveData.value?.data

                HOTEL_VIDEO_URL =
                    response?.httpStreamingHotelvideoUrl + response?.hotelChannelList?.get(0)?.fileName

                initializePlayer()

                binding.tvGreeting.text = response?.hotelInfo
                val btnListFromApi: List<String>? = response?.buttonsList?.map {
                    it.buttonName
                }
                val btnModelList: List<BtnModel> = Constants.HOME_PAGE_MENU_BUTTON_LIST.filter {
                    btnListFromApi?.contains(it.btnId) == true
                }

                val sortedBtnModelList: List<BtnModel> = btnModelList.sortedBy {
                    btnListFromApi?.indexOf(it.btnId) ?: Int.MAX_VALUE
                }

                binding.rvMenuButton.layoutManager = GridLayoutManager(this, 4)
                val adapter = MainMenuBtnAdapter { btn ->
                    val bundle = Bundle()
                    bundle.putString("title", btn.title)
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
                        mainMenuViewModel.themeLiveData.value?.data?.gradientColor
                    )
                    bundle.putString(
                        "gradientEndColor",
                        mainMenuViewModel.themeLiveData.value?.data?.spotLightColor
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
                            intent = Intent(this, ProgramGuideActivity::class.java)
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
                if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
                    adapter.setGradientColor(gradientStartColor, gradientEndColor)
                }
                binding.rvMenuButton.adapter = adapter
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { mainMenuViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleValidateSessionResponse(status: Boolean) {
//        val response = mainMenuViewModel.isGuestCheckedInLiveData.value ?: false

        if (status) {
            mainMenuViewModel.getGuestDetails(guestDetailsDatastore)
        } else {
            binding.tvWelcome.text = ""
            binding.tvWelcome.toInvisible()
        }
        binding.pbLoader.toInvisible()


    }

    private fun handleGuestDetailsResponse(status: Resource<CmdDataDto>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val response = mainMenuViewModel.guestDetailsLiveData.value?.data

                binding.tvWelcome.text = response?.message

                binding.pbLoader.toInvisible()
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
