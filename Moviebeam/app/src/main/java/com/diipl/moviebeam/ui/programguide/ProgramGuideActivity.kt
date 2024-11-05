package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.tv.TvContract
import android.media.tv.TvInputManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.HotelChannel
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.program.DvbChannel
import com.diipl.moviebeam.data.dto.remote.BTCommandModel
import com.diipl.moviebeam.data.dto.remote.IRFrequencyModel
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityProgramGuideBinding
import com.diipl.moviebeam.databinding.DialogSearchProgramBinding
import com.diipl.moviebeam.service.remote.BTService
import com.diipl.moviebeam.service.remote.IIrService
import com.diipl.moviebeam.service.remote.UsbIrService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.LiveTVActivity
import com.diipl.moviebeam.ui.exoplayer.LiveTVActivity.Companion.mChannelList
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.DTV_INPUT_ID
import com.diipl.moviebeam.utils.Constants.DTV_KIT_PACKAGE_NAME
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showKeyboard
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

private const val TAG = "ProgramGuideActivity"

@AndroidEntryPoint
class ProgramGuideActivity : BaseActivity() {

    private lateinit var binding: ActivityProgramGuideBinding
    private val programGuideViewModel: ProgramGuideViewModel by viewModels()

    private var channelContent: List<String?>? = null
    private var channelList: List<ChannelEpgDTO>? = null
    private var channelListNext: List<ChannelEpgDTO>? = null
    private var cNo: String? = null
    private var channelIndex = 0
    private var isFScreenExit = false
    private var previousKey: String? = fetchCurrentProgramKey()
    private var key: String? = fetchCurrentProgramKey()
    private var nextKey: String? = null

    private var previousPrograms: List<ChannelEpgDTO>? = null
    private var currentPrograms: List<ChannelEpgDTO>? = null
    private var nextPrograms: List<ChannelEpgDTO>? = null

    private lateinit var hotelChannel: HotelChannel
    private var hotelChannelVideo: String = ""
    private var isEpgApiCalled = false

    //Variables from datastore
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            applicationContext
        )
    }
    private var ua = ""

    @Inject
    lateinit var preferences: SharedPreference

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

    @Inject
    lateinit var roomRepository: RoomRepository

    private var irService: IIrService? = null
    private lateinit var btService: BTService
    private var switchedToTV = false

    override fun observeViewModel() {
        observeSnackBarMessages(programGuideViewModel.showSnackBar)
        observeToast(programGuideViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityProgramGuideBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        fetchDetails()
        this.getChannelsFromRoomDB()
        setContentView(binding.root)
        binding.btnBack.handleFocusChange()
        binding.btnSearch.handleFocusChange()
        binding.btnBack.setOnClickListener { finish() }
        binding.pbLoader.toVisible()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeDatastoreParams()

        if (preferences.isIRRemote)
            irService = UsbIrService(this).also { it.getInstance() }
        else
            btService = BTService(this, lifecycle).also { it.findBondedDevice() }

    }


    override fun onResume() {
        super.onResume()
        if (isFScreenExit) {
            playChannelVideoBg(null)
        }

        fetchTVChannels()

        binding.btnSearch.setOnKeyListener { view, code, keyEvent ->
            when (code) {
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                    if (view.isFocused) {
                        showSearchDialog()
                        view.clearFocus()
                    }
                }
            }
            false
        }

        setOnScrollListener()

    }

    private fun getChannelsFromRoomDB() {
        programGuideViewModel.getAllChannels(key).observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                currentPrograms = data
                loadProgramGuide(false, data)
                binding.layoutProgramGuide.layoutPrgGuide.rvChannel.post {
                    binding.cvProgramGuide.toVisible()
                    binding.layoutProgramGuide.layoutPrgGuide.rvChannel.findViewHolderForAdapterPosition(
                        0
                    )?.itemView?.requestFocus()
//                    setOnScrollListener()

                }
                setNextPrograms()
                binding.pbLoader.toInvisible()
            } else {
                if (!isEpgApiCalled) {
                    binding.pbLoader.toVisible()
                    isEpgApiCalled = true
                    programGuideViewModel.fetchEPGDataFromServer(ua)
                } else {
                    binding.pbLoader.toInvisible()
                }
//                programGuideViewModel.showToastMessage(getString(R.string.please_contact_the_front_desk_for_assistance))
            }
        }
    }

    private fun setOnScrollListener() {
        val recyclerView1 = binding.layoutProgramGuide.layoutPrgGuide.rvProgram
        val recyclerView2 = binding.layoutProgramGuide.layoutPrgGuide.rvChannel

        val scrollListeners = arrayOfNulls<RecyclerView.OnScrollListener>(2)
        scrollListeners[0] = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView2.removeOnScrollListener(
                    scrollListeners[1]!!
                )
                recyclerView2.scrollBy(dx, dy)
                recyclerView2.addOnScrollListener(
                    scrollListeners[1]!!
                )
            }
        }
        scrollListeners[1] = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView1.removeOnScrollListener(
                    scrollListeners[0]!!
                )
                recyclerView1.scrollBy(dx, dy)
                recyclerView1.addOnScrollListener(
                    scrollListeners[0]!!
                )
            }
        }
//        recyclerView1.addOnScrollListener(scrollListeners[0]!!)
//        recyclerView2.addOnScrollListener(scrollListeners[1]!!)

        recyclerView1.addOnScrollListener(createScrollListener(recyclerView2))
        recyclerView2.addOnScrollListener(createScrollListener(recyclerView1))

    }

    private var isScrolling = false
    private fun createScrollListener(otherRecyclerView: RecyclerView): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isScrolling) return
                isScrolling = true
                otherRecyclerView.scrollBy(dx, dy)
                isScrolling = false
            }
        }
    }


    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogBinding =
            DialogSearchProgramBinding.inflate(LayoutInflater.from(applicationContext))
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialogBinding.etSearch.requestFocus()
        dialogBinding.etSearch.showKeyboard()
        dialogBinding.etSearch.handleFocusChange()

        dialogBinding.etSearch.setOnEditorActionListener { textView, id, keyEvent ->
            when (id) {
                EditorInfo.IME_ACTION_DONE -> {
                    dialog.dismiss()
                    dialogBinding.etSearch.hideKeyboard()
                    searchInAdapter(textView.text.toString().trim())
                    binding.layoutProgramGuide.layoutPrgGuide.rvProgram.requestFocus()
                }
            }
            false
        }
    }

    private fun searchInAdapter(name: String) {
        val adapter = binding.layoutProgramGuide.layoutPrgGuide.rvChannel.adapter as ChannelAdapter
        val list = adapter.getChannelList()
        var focusIndex = -1
        run breaking@{
            list?.forEachIndexed { index, model ->
                if (name.isNotEmpty()) {
                    if ((model.CN?.contains(name, true) == true) or (model.CNO.toString()
                            .contains(name, true))
                    ) {
                        focusIndex = index
                        return@breaking
                    }
                }
            }
        }

        if (focusIndex >= 0) {
            binding.layoutProgramGuide.layoutPrgGuide.rvChannel.scrollToPosition(focusIndex)
            binding.layoutProgramGuide.layoutPrgGuide.rvProgram.scrollToPosition(focusIndex)
        } else programGuideViewModel.showToastMessage("No such channel with $name")
        adapter.updateFocus(focusIndex)

    }

    private fun fetchDetails() {
        intent.extras?.getString("hotelChannel")?.let {
            hotelChannel = it.fromJson()
        }
        intent.extras?.getString("hotelChannelVideo")?.let {
            hotelChannelVideo = it
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun playChannelVideoBg(program: ChannelEpgDTO?) {
        updatePopupText(program, binding.tvPopupProgText)
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            if (this.cNo != program.CNO) {
//                initializePlayer(program)
//                binding.layoutVideo.root.toVisible()
                binding.tvProgramTitle.text = program.P1_PT
                binding.tvDescription.text = program.P1_SY
                this.cNo = program.CNO
                channelIndex = if (channelListNext != null) {
                    channelListNext?.indexOf(program) ?: 0
                } else {
                    channelList?.indexOf(program) ?: 0
                }
            }
        }
    }

    private fun onProgramFocused(program: ChannelEpgDTO?, title: String?, synopsis: String?) {
//        binding.tvProgramTitle.text = title ?: program?.P1_PT
//        binding.tvDescription.text = synopsis ?: program?.P1_SY
        updatePopupText(program, binding.tvPopupProgText)
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            if (this.cNo != program.CNO) {
//                initializePlayer(program)
//                binding.layoutVideo.root.toVisible()
                if (title != null)
                    binding.tvProgramTitle.text = title
                else
                    binding.tvProgramTitle.text = program.P1_PT
                if (synopsis != null)
                    binding.tvDescription.text = synopsis
                else
                    binding.tvDescription.text = program.P1_SY
                this.cNo = program.CNO
                channelIndex = if (channelListNext != null) {
                    channelListNext?.indexOf(program) ?: 0
                } else {
                    channelList?.indexOf(program) ?: 0
                }
            } else {
                binding.tvProgramTitle.text = title
                binding.tvDescription.text = synopsis
            }
        }
    }

    private fun updatePopupText(channel: ChannelEpgDTO?, tvPopupText: TextView) {
        tvPopupText.text = "Please press the OK button on your remote to tune in to ${channel?.CN}."

        val text = "Press + to return to the Main Menu at any time."

        val spannableString = SpannableString(text)

        val drawable: Drawable = getDrawable(R.drawable.remote_home)!!

        drawable.setBounds(0, 0, 45, 32)

        val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_CENTER)

        spannableString.setSpan(
            imageSpan,
            text.indexOf('+'),
            text.indexOf('+') + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvPopupProgDesc.text = spannableString

        binding.tvPopupProgDesc.textAlignment = View.TEXT_ALIGNMENT_CENTER

    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun initializePlayer(program: ChannelEpgDTO?) {
        val player = ExoPlayer.Builder(this)
            .setRenderersFactory(DefaultRenderersFactory(this).setEnableDecoderFallback(true))
            .build()
        val playerView = binding.layoutVideo.videoView
        playerView.player?.release()
        playerView.player = player
        player.let {
            program?.VP?.let { vp ->
                it.setMediaItem(MediaItem.fromUri(vp))
            }
            it.playWhenReady = true
            it.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            it.prepare()
            it.play()
        }
    }

    private fun launchExoPlayer(program: ChannelEpgDTO?) {
        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_CHROMECAST -> {
                if (preferences.isIRRemote) switchToTV(program)
                else switchToTVWithBluetooth(program)
            }
            Constants.BUILD_TYPE_STB -> {
                tuneChannels(program)
            }
        }
    }

    private fun switchToTV(program: ChannelEpgDTO?) {
        clearCache()
        lifecycleScope.launch {
            val model = preferences.irFrequencyModel
            irService?.let { service ->
                if (service.isConnected()){
                    val num = program?.CNO.toString().toCharArray().asList()
                    if (model.tvBrandName != IRUtils.LG) {
                        service.transmit(model.frequency, model.TV)
                        delay(model.delayMs)
                    }
                    switchedToTV = true
                    when (num.size) {
                        4 -> {
                            launch {
                                num[num.size - 4].sendPacket(model)
                                num[num.size - 3].sendPacket(model)
                                num[num.size - 2].sendPacket(model)
                                num[num.size - 1].sendPacket(model)
//                            delay(240)
                                service.transmit(model.frequency, model.OK)
                            }
                        }

                        3 -> {
                            launch {
                                num[num.size - 3].sendPacket(model)
                                num[num.size - 2].sendPacket(model)
                                num[num.size - 1].sendPacket(model)
//                            delay(240)
                                service.transmit(model.frequency, model.OK)
                            }
                        }

                        2 -> {
                            launch {
                                num[num.size - 2].sendPacket(model)
                                num[num.size - 1].sendPacket(model)
//                            delay(240)
                                service.transmit(model.frequency, model.OK)
                            }
                        }

                        1 -> {
                            num[0].sendPacket(model)
                            service.transmit(model.frequency, model.OK)
                        }
                    }
                }
            }

        }
    }

    private fun Char.sendPacket(model: IRFrequencyModel) {
        val nValue = when (this) {
            '1' -> model.tv1
            '2' -> model.tv2
            '3' -> model.tv3
            '4' -> model.tv4
            '5' -> model.tv5
            '6' -> model.tv6
            '7' -> model.tv7
            '8' -> model.tv8
            '9' -> model.tv9
            else -> model.tv0
        }
        irService?.transmit(model.frequency, nValue)
    }

    private fun switchToTVWithBluetooth(program: ChannelEpgDTO?) {
        clearCache()
        lifecycleScope.launch {
            val model = preferences.btCommandModel
            val num = program?.CNO.toString().toCharArray().asList()

            Log.e(TAG, "switchToTVWithBluetooth: ${btService.isConnected()}")

            if (btService.isConnected()) {
                if (model.tvBrandName != IRUtils.LG) {
                    btService.transmit(model.TV)
                    delay(model.delayMs)
                }
                switchedToTV = true
                when (num.size) {
                    4 -> {
                        launch {
                            num[num.size - 4].sendBleCode(model)
                            delay(1000)
                            num[num.size - 3].sendBleCode(model)
                            delay(1000)
                            num[num.size - 2].sendBleCode(model)
                            delay(1000)
                            num[num.size - 1].sendBleCode(model)
                        }
                    }

                    3 -> {
                        launch {
                            num[num.size - 3].sendBleCode(model)
                            delay(1000)
                            num[num.size - 2].sendBleCode(model)
                            delay(1000)
                            num[num.size - 1].sendBleCode(model)
                        }
                    }

                    2 -> {
                        launch {
                            num[num.size - 2].sendBleCode(model)
                            delay(1000)
                            num[num.size - 1].sendBleCode(model)
                        }
                    }

                    1 -> {
                        num[0].sendBleCode(model)
                    }
                }
            } else {
                val msg = BTService.MSG_BT_NOT_CONNECTED
                Log.e(TAG, "switchToTVWithBluetooth: $msg")
                showToast(msg)
            }


        }
    }

    private fun Char.sendBleCode(model: BTCommandModel) {
        val nValue = when (this) {
            '1' -> model.tv1
            '2' -> model.tv2
            '3' -> model.tv3
            '4' -> model.tv4
            '5' -> model.tv5
            '6' -> model.tv6
            '7' -> model.tv7
            '8' -> model.tv8
            '9' -> model.tv9
            else -> model.tv0
        }
        btService.transmit(nValue)
    }

    //    Switch from Live TV to HDMI 1 only
    fun switchToHDMI() {
        if (preferences.isIRRemote) {
            val model = preferences.irFrequencyModel
            irService?.transmit(model.frequency, model.HDMI1)
            switchedToTV = false
        } else {
            if (btService.isConnected()){
                val model = preferences.btCommandModel
                btService.transmit(model.HDMI1)
                switchedToTV = false
            } else showToast(BTService.MSG_BT_NOT_CONNECTED)
        }
        Log.e(TAG, "switchToHDMI  $switchedToTV")
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        when (keyCode) {
//            KeyEvent.KEYCODE_BACK -> {
//                onBackPressed()
//            }
//        }
//        return false
//    }

    override fun onBackPressed() {
        lifecycleScope.launch {
            delay(1000)
            if (switchedToTV) {
                switchToHDMI()
            } else {
                super.onBackPressed()
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.layoutVideo.videoView.player?.release()
    }

    private fun loadProgramGuide(
        isScrolled: Boolean = false,
        currentPrograms: MutableList<ChannelEpgDTO>? = null
    ) {

        val currentProgram = currentPrograms?.get(0)
        binding.layoutProgramGuide.tvTime1.text = currentProgram?.P1_DST
        binding.layoutProgramGuide.tvTime2.text = currentProgram?.P2_DST
        binding.layoutProgramGuide.tvTime3.text = currentProgram?.P3_DST
        binding.layoutProgramGuide.tvTime4.text = currentProgram?.P4_DST

        currentPrograms?.remove(currentProgram)
        val hotelVideoProgram = ChannelEpgDTO(
            CN = hotelChannel.channelName,
            VP = hotelChannelVideo,
            CNO = hotelChannel.channelNo,
            P1_PT = hotelChannel.channelName,
            P1_CLS = "80",
            C = "1"
        )

        Log.e(TAG, "loadProgramGuide: $hotelVideoProgram")

        currentPrograms?.add(0, hotelVideoProgram)

        if (!isScrolled) {
            channelList = currentPrograms
            CURRENT_PROGRAMS = currentPrograms
            channelContent = currentPrograms?.map { it.VP }
            setUpChannels(currentPrograms)
        } else
            channelListNext = currentPrograms
        setUpPrograms(currentPrograms, currentProgram?.P4_DST)

        setUpChannels(currentPrograms)
        setUpPrograms(currentPrograms, currentProgram?.P4_DST)
    }

    private fun setUpChannels(channelList: List<ChannelEpgDTO>?) {
        val adapter = ChannelAdapter(
            onChannelFocused = ::playChannelVideoBg,
            onChannelClicked = ::launchExoPlayer
        )
        adapter.setChannelList(channelList)
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.layoutManager =
            LinearLayoutManager(this)
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.adapter = adapter
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.setHasFixedSize(true)

    }

    private fun setUpPrograms(programsList: List<ChannelEpgDTO>?, p4Dst: String?) {
        val adapter = ProgramsAdapter(
            onProgramFocused = ::onProgramFocused,
            onProgramClicked = ::launchExoPlayer,
            loadNextPrograms = ::loadNextPrograms,
            loadPreviousPrograms = ::loadPreviousPrograms
        )
        adapter.setProgramList(programsList)
        adapter.setProg4Dst(p4Dst)
        binding.layoutProgramGuide.layoutPrgGuide.rvProgram.layoutManager =
            LinearLayoutManager(this)
        binding.layoutProgramGuide.layoutPrgGuide.rvProgram.adapter = adapter
//        binding.layoutProgramGuide.layoutPrgGuide.rvProgram.setHasFixedSize(true)
    }

    private fun loadPreviousPrograms() {
        if (!previousPrograms.isNullOrEmpty()) {
            loadProgramGuide(true, previousPrograms?.toMutableList())
            requestFocusOnProgram()
            nextPrograms = currentPrograms
            currentPrograms = previousPrograms
            previousPrograms = null
            nextKey = key
            key = previousKey
            previousKey = null
            setPreviousPrograms()
        }
    }

    private fun loadNextPrograms() {
        if (!nextPrograms.isNullOrEmpty()) {
            loadProgramGuide(true, nextPrograms?.toMutableList())
            requestFocusOnProgram()
            previousPrograms = currentPrograms
            currentPrograms = nextPrograms
            nextPrograms = null
            previousKey = key
            key = nextKey
            nextKey = null
            setNextPrograms()
        }
    }

    private fun setPreviousPrograms() {
        val dateFormatter = SimpleDateFormat("ddMMyyyyhhmma", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = key?.let { dateFormatter.parse(it) }!!
        cal.add(Calendar.HOUR_OF_DAY, -2)
        previousKey = fetchCurrentProgramKey(cal)
        programGuideViewModel.getAllChannels(previousKey!!).observe(this) { data ->
            previousPrograms = data
        }
    }

    private fun setNextPrograms() {
        val dateFormatter = SimpleDateFormat("ddMMyyyyhhmma", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = key?.let { dateFormatter.parse(it) }!!
        cal.add(Calendar.HOUR_OF_DAY, 2)
        nextKey = fetchCurrentProgramKey(cal)
        programGuideViewModel.getAllChannels(nextKey!!).observe(this) { data ->
            nextPrograms = data
        }
    }

    private fun requestFocusOnProgram() {
        binding.layoutProgramGuide.layoutPrgGuide.rvProgram.post {
            binding.layoutProgramGuide.layoutPrgGuide.rvProgram.findViewHolderForAdapterPosition(
                channelIndex
            )?.itemView?.post {
                val recyclerView =
                    binding.layoutProgramGuide.layoutPrgGuide.rvProgram.findViewHolderForAdapterPosition(
                        channelIndex
                    )?.itemView as RecyclerView
                recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
            }
        }
    }

    private fun hasReadTvListings(context: Context): Boolean {
        return (context.checkSelfPermission("android.permission.READ_TV_LISTINGS")
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun loadChannelList() {
        DTV_INPUT_ID = findDvbInput() ?: return

        val projection = arrayOf(
            TvContract.Channels._ID,
            TvContract.Channels.COLUMN_INPUT_ID,
            TvContract.Channels.COLUMN_SERVICE_ID,
            TvContract.Channels.COLUMN_SERVICE_TYPE,
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContract.Channels.COLUMN_DISPLAY_NUMBER,
            TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
            TvContract.Channels.COLUMN_VIDEO_FORMAT,
            TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
            TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA
        )

        val cursor = contentResolver.query(
            TvContract.Channels.CONTENT_URI, projection,
            null, null,
            "${TvContract.Channels.COLUMN_DISPLAY_NUMBER} ASC"
        )

        mChannelList.clear()

        while (cursor?.moveToNext() == true) {
            var index = 0
            val channelId = cursor.getLong(index++)
            val curInputId = cursor.getString(index++)
            val serviceId = cursor.getString(index++)
            val serviceType = cursor.getString(index++)
            val displayName = cursor.getString(index++)
            val displayNumber = cursor.getString(index++)
            val streamID = cursor.getString(index++)
            val format = cursor.getString(index++)
            val networkID = cursor.getString(index++)
            val blob = cursor.getBlob(index++)

            // only consider dvb input
            if (DTV_INPUT_ID != curInputId)
                continue

            // only keep AUDIO_VIDEO services
            if (TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO != serviceType)
                continue

            // skip channels without name or number
            if (displayName == null || displayNumber == null)
                continue

            val channelNumber = displayNumber.toInt()

            val channel = DvbChannel(
                displayName,
                channelNumber,
                channelId,
                curInputId
            )

            mChannelList.add(channel)

        }
        cursor?.close()

        if (mChannelList.isEmpty()) {
            val msg = "Unable to find any dvb channels, are channel searchable ?"
            Log.e(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return
        }

        mChannelList.sortBy { dvbChannel -> dvbChannel.number }
    }

    private fun findDvbInput(): String? {
        val mTvInputManager = getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager

        Log.i(TAG, "============================================")
        Log.i(TAG, "enumerate tv input")
        var dvbInputFound = false
        var dtvInputComponent = ""
        mTvInputManager?.let {
            for (tvInputInfo in it.tvInputList) {
                if (tvInputInfo.id.startsWith("${DTV_KIT_PACKAGE_NAME}/")) {
                    dvbInputFound = true
                    dtvInputComponent = tvInputInfo.id
                }
            }
        }

        Log.i(TAG, "============================================")

        if (!dvbInputFound) {
            val msg = "Failed to find dvb input"
            Log.e(TAG, msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return null
        }

        return dtvInputComponent
    }

    private fun fetchTVChannels() {
        if (hasReadTvListings(this)) {
            loadChannelList()
        } else {
            requestPermissions(arrayOf("android.permission.READ_TV_LISTINGS"), 1001)
        }
    }

    private fun tuneChannels(program: ChannelEpgDTO?) {
        if (!program?.CN.equals("Hotel Video")) {
            val intent = Intent(applicationContext, LiveTVActivity::class.java)
            intent.putExtra("currentPos", 99)
            startActivity(intent)
        } else {
            showToast("Not Available")
        }
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            ua = preferenceHandler.UA
        }
    }

    companion object {
        var CURRENT_PROGRAMS: List<ChannelEpgDTO>? = null
    }

}
