package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.tv.TvContract
import android.media.tv.TvInputManager
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.HotelChannel
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.epg.EPGResponse
import com.diipl.moviebeam.data.dto.program.DvbChannel
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.remote.FrequencyModel
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.data.repositories.RoomRepository
import com.diipl.moviebeam.databinding.ActivityProgramGuideBinding
import com.diipl.moviebeam.databinding.DialogSearchProgramBinding
import com.diipl.moviebeam.service.IIrService
import com.diipl.moviebeam.service.UsbIrService
import com.diipl.moviebeam.service.isCompatibleDevice
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.LiveTVActivity
import com.diipl.moviebeam.ui.exoplayer.LiveTVActivity.Companion.mChannelList
import com.diipl.moviebeam.ui.splash.BlankActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.DTV_KIT_PACKAGE_NAME
import com.diipl.moviebeam.utils.Constants.DVB_INPUT_ID
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.isEpgDataValid
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.removeEarlierData
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showKeyboard
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInteger
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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

    private val usbManager: UsbManager by lazy { getSystemService(USB_SERVICE) as UsbManager }
    private lateinit var usbDevice: UsbDevice

    override fun observeViewModel() {
        observe(programGuideViewModel.epgLiveData, ::handleEpgResponse)
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
        programGuideViewModel.getChannelListResponseData(channelListDataStore)
        initSet()
    }

    private fun initSet() {
        usbManager.deviceList.values.forEach {
            if (isCompatibleDevice(it)) {
                usbDevice = it
                val isOk = usbManager.hasPermission(usbDevice)
                if (isOk) {
                    irService = UsbIrService.getInstance(usbManager, usbDevice)
                } else {
                    val i = Intent(this, BlankActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(i)
                }
            }
        }
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
//        if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_CHROMECAST, true)) {
        /* } else if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_MINI_BOX, true)) {
             binding.layoutVideo.videoView.player?.pause()
             val bundle = Bundle()
             bundle.putStringArrayList(
                 Constants.CONTENT_LIST_PARAM,
                 channelContent as ArrayList<String?>
             )
             bundle.putInt(Constants.SELECTED_CHANNEL_INDEX, channelList?.indexOf(program) ?: 0)
             bundle.putString(Constants.CHANEL_NO_PARAM, program?.CNO)
             bundle.putString(Constants.CHANNEL_NAME_PARAM, program?.CN)
             bundle.putString(Constants.CHANNEL_LOGO_PARAM, program?.CL)
             bundle.putString(Constants.NOW_SHOWING_PARAM, program?.liveProg1)
             bundle.putString(Constants.NEXT_PROGRAM_PARAM, program?.liveProg2)
             bundle.putString(Constants.PROG_1_TIME_PARAM, program?.prog1Time)
             bundle.putString(Constants.PROG_2_TIME_PARAM, program?.prog2Time)
             bundle.putString(Constants.CHANNEL_LIST_PARAM, Gson().toJson(channelList))

             val intent = Intent(this, PrgGuidePlayerActivity::class.java)
             intent.putExtras(bundle)
             startActivity(intent)
             this.isFScreenExit = true
         } else if (BuildConfig.BUILD_TYPE.equals(Constants.BUILD_TYPE_STB, true)) {
             launchLiveTvApp()
         }*/

        when(BuildConfig.BUILD_TYPE){
            Constants.BUILD_TYPE_CHROMECAST -> switchToTV(program)
            Constants.BUILD_TYPE_STB -> tuneChannels(program)
        }

    }


    private fun switchToTV(program: ChannelEpgDTO?) {
        clearCache()
        lifecycleScope.launch {
            var model = preferences.irFrequencyModel
            if (model == null) {
                preferences.irFrequencyModel = IRUtils.SELECTED_BRAND
                model = preferences.irFrequencyModel
            }
            irService?.let { service ->
                val num = program?.CNO/*.plus(100)*/.toString().toCharArray().asList()
                if (model.tvBrandName != IRUtils.LG) {
                    service.transmit(model.frequency, model.TV)
                    delay(model.delayMs)
                }
                when (num.size) {
                    4 -> {
                        launch {
                            num[num.size - 4].sendPacket(model)
                            num[num.size - 3].sendPacket(model)
                            num[num.size - 2].sendPacket(model)
                            num[num.size - 1].sendPacket(model)
                            delay(240)
                            service.transmit(model.frequency, model.OK)
                        }
                    }

                    3 -> {
                        launch {
                            num[num.size - 3].sendPacket(model)
                            num[num.size - 2].sendPacket(model)
                            num[num.size - 1].sendPacket(model)
                            delay(240)
                            service.transmit(model.frequency, model.OK)
                        }
                    }

                    2 -> {
                        launch {
                            num[num.size - 2].sendPacket(model)
                            num[num.size - 1].sendPacket(model)
                            delay(240)
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

    private fun Char.sendPacket(model: FrequencyModel) {
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

    private fun handleEpgResponse(status: Resource<EPGResponse>) {
        when (status) {
            is Resource.Success -> {
                //Removing all Epg Channels From RoomDB.
                lifecycleScope.launch(Dispatchers.IO) {
                    roomRepository.removeAllChannels()
                }

                val simpleDateFormatter =
                    SimpleDateFormat(Constants.EPG_DATE_FORMAT, Locale.ENGLISH)
                status.data?.let {
                    if (isEpgDataValid(it.ST, it.ET, simpleDateFormatter)) {
                        updateEpgStAndEt(it.ST, it.ET)
                        val channelList =
                            programGuideViewModel.channelListLiveData.value?.data?.channelLcnList
                        val currentKey = fetchCurrentProgramKey()
                        removeEarlierData(it.epgListMap?.entries?.iterator(), currentKey)
                        for (entries in it.epgListMap?.entries!!) {
                            val iterator = entries.value.iterator()
                            val key = entries.key
                            val ciMap = HashMap<Int, Boolean>()
                            while (iterator.hasNext()) {
                                val channel = iterator.next()
                                channel.key = key
                                if (channel.CI != null) {
                                    var isFound = false
                                    for (channelApi in channelList!!) {
                                        if (channelApi.CI == channel.CI) {
                                            isFound = true
                                            //Mapping EpgMap with Channel List Api
                                            channel.AR = channelApi.AR
                                            channel.CN = channelApi.CN
                                            channel.CNO = channelApi.CNO
                                            channel.CBT = channelApi.CBT
                                            channel.CL = channelApi.CL
                                            channel.CLCloud = channelApi.CLCloud
                                            if (channelApi.httpStreaming == true)
                                                channel.VP = channelApi.httpStreamingUrl
                                            else
                                                channel.VP = channelApi.VP
                                            channel.param1 = channelApi.param1
                                            channel.param2 = channelApi.param2
                                            channel.httpStreamingUrl = channelApi.httpStreamingUrl
                                            channel.httpStreaming = channelApi.httpStreaming
                                            channel.recordable = channelApi.recordable

                                            channel.channelNameNo =
                                                "${channelApi.CNO}   ${channelApi.CN}"
                                            channel.lastProg = channel.C
                                            channel.prog1Time =
                                                "${channel.P1_ST} - ${channel.P1_ET}"

                                            //Mapping EpgMap with Program Map Api
                                            if (channel.P1_ID != null) {
                                                val program1 =
                                                    it.programsListMap?.get(channel.P1_ID)
                                                if (program1 != null) {
                                                    channel.P1_PT = program1.PT
                                                    channel.P1_SY = program1.SY
                                                    channel.progInfo = program1.PT
                                                    channel.progSynopsis = program1.SY
                                                    channel.liveProg1 = program1.PT
                                                    channel.progInfo1 =
                                                        "${channel.CNO} - ${program1.PT}"
                                                } else {
                                                    channel.P1_PT =
                                                        Constants.NO_INFORMATION_AVAILABLE
                                                    channel.P1_SY =
                                                        Constants.NO_INFORMATION_AVAILABLE
                                                    channel.progInfo =
                                                        Constants.NO_INFORMATION_AVAILABLE
                                                    channel.progSynopsis =
                                                        Constants.NO_INFORMATION_AVAILABLE
                                                    channel.liveProg1 =
                                                        Constants.NO_INFORMATION_AVAILABLE
                                                }
                                            }
                                            // for live tv and full screen (Next)
                                            if (channel.C?.toInteger()!! > 1) {
                                                if (channel.P2_ID != null) {
                                                    val program2 =
                                                        it.programsListMap?.get(channel.P2_ID)
                                                    channel.P2_PT = program2?.PT
                                                    channel.P2_SY = program2?.SY
                                                    channel.liveProg2 = program2?.PT
                                                    channel.progInfo2 =
                                                        "${channel.CNO} - ${program2?.PT}"
                                                    channel.prog2Time =
                                                        "${channel.P2_ST} - ${channel.P2_ET}"
                                                }
                                                if (channel.P3_ID != null) {
                                                    val program3 =
                                                        it.programsListMap?.get(channel.P3_ID)
                                                    channel.P3_PT = program3?.PT
                                                    channel.P3_SY = program3?.SY
                                                }
                                                if (channel.P4_ID != null) {
                                                    val program4 =
                                                        it.programsListMap?.get(channel.P4_ID)
                                                    channel.P4_PT = program4?.PT
                                                    channel.P4_SY = program4?.SY
                                                }
                                                if (channel.P5_ID != null) {
                                                    val program5 =
                                                        it.programsListMap?.get(channel.P5_ID)
                                                    channel.P5_PT = program5?.PT
                                                    channel.P5_SY = program5?.SY
                                                }
                                                if (channel.P6_ID != null) {
                                                    val program6 =
                                                        it.programsListMap?.get(channel.P6_ID)
                                                    channel.P6_PT = program6?.PT
                                                    channel.P6_SY = program6?.SY
                                                }
                                                if (channel.P7_ID != null) {
                                                    val program7 =
                                                        it.programsListMap?.get(channel.P7_ID)
                                                    channel.P7_PT = program7?.PT
                                                    channel.P7_SY = program7?.SY
                                                }
                                                if (channel.P8_ID != null) {
                                                    val program8 =
                                                        it.programsListMap?.get(channel.P8_ID)
                                                    channel.P8_PT = program8?.PT
                                                    channel.P8_SY = program8?.SY
                                                }
                                            } else {
                                                //Calculating next Program Time from program1 end Time when Only One Program is Available
                                                val nextProgramTime = Calendar.getInstance()
                                                nextProgramTime.time =
                                                    simpleDateFormatter.parse(channel.P1_DET)
                                                val nextProgramKey =
                                                    fetchCurrentProgramKey(nextProgramTime)
                                                val nextProgram: ChannelEpgDTO? =
                                                    it.epgListMap[nextProgramKey]?.first {
                                                        it.CI == channelApi.CI
                                                    }
                                                when (nextProgramTime.get(Calendar.MINUTE)) {
                                                    0, 30 -> {
                                                        channel.prog2Time =
                                                            "${nextProgram?.P1_ST} - ${channel.P1_ET}"
                                                        channel.liveProg2 =
                                                            it.programsListMap?.get(nextProgram?.P1_ID)?.PT
                                                    }

                                                    else -> {
                                                        channel.prog2Time =
                                                            "${channel.P2_ST} - ${channel.P2_ET}"
                                                        channel.liveProg2 =
                                                            it.programsListMap?.get(nextProgram?.P2_ID)?.PT
                                                    }
                                                }
                                            }
                                            break
                                        }
                                    }
                                    if (!isFound)
                                        iterator.remove()
                                    else {
                                        //Removing Duplicate Channels
                                        if (ciMap[channel.CI] != null)
                                            iterator.remove()
                                        else
                                            ciMap[channel.CI] = true
                                    }
                                }
                            }
                            //Sorting Channels by Channel No
                            entries.value.sortBy { it.CNO?.toInteger() }
                            //Adding Channels to RoomDB.
                            lifecycleScope.launch(Dispatchers.IO) {
                                roomRepository.insertChannels(entries.value)
                            }
                        }
                        this.getChannelsFromRoomDB()
                    } else {
                        //TODO invalid Data found
                        binding.pbLoader.toInvisible()
                        programGuideViewModel.showToastMessage(getString(R.string.please_contact_the_front_desk_for_assistance))
                    }
                }
            }

            else -> {
                status.errorCode?.let { programGuideViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { programGuideViewModel.showToastMessage(it) }
            }
        }
    }

    private fun hasReadTvListings(context: Context): Boolean {
        return (context.checkSelfPermission("android.permission.READ_TV_LISTINGS")
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun loadChannelList() {
        DVB_INPUT_ID = findDvbInput() ?: return

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
            if (DVB_INPUT_ID != curInputId)
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

    private fun updateEpgStAndEt(epgStartTime: String?, epgEndTime: String?) {
        lifecycleScope.launch {
            epgStartTime?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.EPG_START_TIME_KEY,
                    it
                )
            }
            epgEndTime?.let {
                preferenceDataStoreHelper.putPreference(
                    PreferenceDataStoreConstants.EPG_END_TIME_KEY,
                    it
                )
            }
        }
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            ua = getUa()
        }
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    companion object {
        var CURRENT_PROGRAMS: List<ChannelEpgDTO>? = null
    }

}
