package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.remote.FrequencyModel
import com.diipl.moviebeam.databinding.ActivityDisconnectedPrgBinding
import com.diipl.moviebeam.service.IIrService
import com.diipl.moviebeam.service.UsbIrService
import com.diipl.moviebeam.service.isCompatibleDevice
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.splash.BlankActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val TAG = "DisconnectedPrgActivity"


@AndroidEntryPoint
class DisconnectedPrgActivity : BaseActivity() {

    private lateinit var binding: ActivityDisconnectedPrgBinding
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

    @Inject
    lateinit var preferences : SharedPreference
    private var irService: IIrService? = null

    private val usbManager: UsbManager by lazy { getSystemService(USB_SERVICE) as UsbManager }
    private lateinit var usbDevice: UsbDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun observeViewModel() {
        observeSnackBarMessages(programGuideViewModel.showSnackBar)
        observeToast(programGuideViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityDisconnectedPrgBinding.inflate(layoutInflater)
        this.getChannelsFromRoomDB()
        fetchDetails()
        setContentView(binding.root)
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { finish()

        }
//        binding.pbLoader.toVisible()
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
            } else {
                programGuideViewModel.showToastMessage(getString(R.string.please_contact_the_front_desk_for_assistance))
            }
        }
//        binding.pbLoader.toInvisible()
    }

    override fun onResume() {
        super.onResume()
        if (isFScreenExit) {
            playChannelVideoBg(null)
        }

        setOnScrollListener()

    }

    private fun setOnScrollListener() {
//        val recyclerView1 = binding.layoutProgramGuide.layoutPrgGuide.rvProgram
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
//                recyclerView1.removeOnScrollListener(
//                    scrollListeners[0]!!
//                )
//                recyclerView1.scrollBy(dx, dy)
//                recyclerView1.addOnScrollListener(
//                    scrollListeners[0]!!
//                )
            }
        }
//        recyclerView1.addOnScrollListener(createScrollListener(recyclerView2))
//        recyclerView2.addOnScrollListener(createScrollListener(recyclerView1))

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

    private fun fetchDetails() {
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        intent.extras?.let {
            binding.layoutHeader.tvTitle.text = it.getString(Constants.TITLE_PARAM)
            loadBg(it.getString("themeBackgroundFileName"))
        }
    }

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    resource.alpha = 120
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun appendZeros(value: Int): String {
        val str = StringBuffer(value.toString()).reverse()
        str.append("0")
        return str.reverse().toString()
    }

    private fun playChannelVideoBg(program: ChannelEpgDTO?) {
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            if (this.cNo != program.CNO) {
//                initializePlayer(program)
//                binding.layoutVideo.root.toVisible()
                this.cNo = program.CNO
                if (channelListNext != null) {
                    channelIndex = channelListNext?.indexOf(program) ?: 0
                } else {
                    channelIndex = channelList?.indexOf(program) ?: 0
                }
            }
        }
    }

    private fun onProgramFocused(program: ChannelEpgDTO?, title: String?, synopsis: String?) {
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            if (this.cNo != program.CNO) {
                initializePlayer(program)
                binding.layoutVideo.root.toVisible()
                this.cNo = program.CNO
                if (channelListNext != null) {
                    channelIndex = channelListNext?.indexOf(program) ?: 0
                } else {
                    channelIndex = channelList?.indexOf(program) ?: 0
                }
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializePlayer(program: ChannelEpgDTO?) {
        val player = ExoPlayer.Builder(this)
            .setRenderersFactory(DefaultRenderersFactory(this).setEnableDecoderFallback(true))
            .build()
        val playerView = binding.layoutVideo.videoView
        playerView.player?.release()
        playerView.player = player
        player?.let {
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
        switchToTV(program)
       /* binding.layoutVideo.videoView.player?.pause()
        val bundle = Bundle()
        bundle.putStringArrayList(
            Constants.CONTENT_LIST_PARAM,
            channelContent as ArrayList<String?>?
        )
        if (channelListNext != null) {
            bundle.putInt(Constants.SELECTED_CHANNEL_INDEX, channelListNext?.indexOf(program) ?: 0)
        } else {
            bundle.putInt(Constants.SELECTED_CHANNEL_INDEX, channelList?.indexOf(program) ?: 0)
        }
        bundle.putString(Constants.CHANEL_NO_PARAM, program?.CNO)
        bundle.putString(Constants.CHANNEL_NAME_PARAM, program?.CN)
        bundle.putString(Constants.CHANNEL_LOGO_PARAM, program?.CL)
        bundle.putString(Constants.NOW_SHOWING_PARAM, program?.liveProg1)
        bundle.putString(Constants.NEXT_PROGRAM_PARAM, program?.liveProg2)
        bundle.putString(Constants.PROG_1_TIME_PARAM, program?.prog1Time)
        bundle.putString(Constants.PROG_2_TIME_PARAM, program?.prog2Time)

        val intent = Intent(this, PrgGuidePlayerActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        this.isFScreenExit = true*/
    }

    private fun switchToTV(program: ChannelEpgDTO?) {
        clearCache()
        lifecycleScope.launch {
            var model = preferences.irFrequencyModel
            if (model == null){
                preferences.irFrequencyModel = IRUtils.SELECTED_BRAND
                model = preferences.irFrequencyModel
            }
            irService?.let { service->
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
                        num[num.size - 1].sendPacket(model)
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
    override fun onDestroy() {
        super.onDestroy()
        binding.layoutVideo.videoView.player?.release()
        Log.e(TAG, "onDestroy: ")
    }

    private fun loadProgramGuide(
        isScrolled: Boolean = false,
        currentPrograms: MutableList<ChannelEpgDTO>? = null
    ) {

        val currentProgram = currentPrograms?.get(0)

        currentPrograms?.remove(currentProgram)
        if (!isScrolled) {
            channelList = currentPrograms
            Constants.CURRENT_PROGRAMS = currentPrograms
            channelContent = currentPrograms?.map { it.VP }
            setUpChannels(currentPrograms)
        } else
            channelListNext = currentPrograms
        setUpPrograms(currentPrograms, currentProgram?.P4_DST)

    }

    private fun fetchCurrentProgramKey(currentDate: Date = Date()): String {
        val cal = Calendar.getInstance()
        cal.time = currentDate
        val date = cal.get(Calendar.DATE)
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        var hour = cal.get(Calendar.HOUR)
        val minutes = cal.get(Calendar.MINUTE)
        val amPm = cal.get(Calendar.AM_PM)
        val time = StringBuilder()

        if (date < 10) time.append(appendZeros(date))
        else time.append(date)

        if (month < 10) time.append(appendZeros(month))
        else time.append(month)

        time.append(year)

        if (hour == 0) hour = 12

        if (hour < 10) time.append(appendZeros(hour))
        else time.append(hour.toString())

        if (minutes < 30) time.append("00")
        else time.append("30")

        if (amPm == 0) time.append("AM")
        else time.append("PM")

        return time.toString()
    }

    private fun setUpChannels(channelList: List<ChannelEpgDTO>?) {
        val adapter = DisconnectedChannelAdapter(
            onChannelFocused = ::playChannelVideoBg,
            onChannelClicked = ::launchExoPlayer
        )
        adapter.setChannelList(channelList)
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.layoutManager =
            LinearLayoutManager(this)
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.adapter = adapter

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
    }

    private fun loadPreviousPrograms() {
        if (!previousPrograms.isNullOrEmpty()) {
            loadProgramGuide(true, previousPrograms?.toMutableList())
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
        cal.time = dateFormatter.parse(key)
        cal.add(Calendar.HOUR_OF_DAY, -2)
        previousKey = fetchCurrentProgramKey(cal.time)
        programGuideViewModel.getAllChannels(previousKey!!).observe(this) { data ->
            previousPrograms = data
        }
    }

    private fun setNextPrograms() {
        val dateFormatter = SimpleDateFormat("ddMMyyyyhhmma", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = dateFormatter.parse(key)
        cal.add(Calendar.HOUR_OF_DAY, 2)
        nextKey = fetchCurrentProgramKey(cal.time)
        programGuideViewModel.getAllChannels(nextKey!!).observe(this) { data ->
            nextPrograms = data
        }
    }



}