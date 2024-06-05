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
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.program.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
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
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private var currentPrograms: List<ChannelEpgDTO>? = null

    @Inject
    lateinit var preferences : SharedPreference

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

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
        observe(programGuideViewModel.channelListLiveData, ::handleChannelListResponse)
        observeSnackBarMessages(programGuideViewModel.showSnackBar)
        observeToast(programGuideViewModel.showToast)
    }

    private fun handleChannelListResponse(status: Resource<ChannelListResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let {
                    Log.e(TAG, "handleChannelListResponse: ${it.channelLcnList}", )
                    setUpChannels(it.channelLcnList)
                }
            }

            else -> {

                status.errorCode?.let { programGuideViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { programGuideViewModel.showToastMessage(it) }

            }
        }
    }

    override fun initViewBinding() {
        binding = ActivityDisconnectedPrgBinding.inflate(layoutInflater)
        fetchDetails()
        setContentView(binding.root)
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { finish() }
        programGuideViewModel.getChannelListResponseData(channelListDataStore)

//        programGuideViewModel.getChannels()
//        this.getChannelsFromRoomDB()
//        binding.pbLoader.toVisible()
    }
//    private fun getChannelsFromRoomDB() {
//        programGuideViewModel.channelList.observe(this) { resource ->
//            resource.data?.channelLcnList?.let { data ->
//                    currentPrograms = data
//                loadProgramGuide(false, data?.toMutableList())
//                binding.layoutProgramGuide.layoutPrgGuide.rvChannel.post {
//                        binding.cvProgramGuide.toVisible()
//                        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
//                    }
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        if (isFScreenExit) {
            playChannelVideoBg(null)
        }
//        setOnScrollListener()
    }

//    private fun setOnScrollListener() {
////        val recyclerView1 = binding.layoutProgramGuide.layoutPrgGuide.rvProgram
//        val recyclerView2 = binding.layoutProgramGuide.layoutPrgGuide.rvChannel
//
//        val scrollListeners = arrayOfNulls<RecyclerView.OnScrollListener>(2)
//        scrollListeners[0] = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                recyclerView2.removeOnScrollListener(
//                    scrollListeners[1]!!
//                )
//                recyclerView2.scrollBy(dx, dy)
//                recyclerView2.addOnScrollListener(
//                    scrollListeners[1]!!
//                )
//            }
//        }
//        scrollListeners[1] = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
////                recyclerView1.removeOnScrollListener(
////                    scrollListeners[0]!!
////                )
////                recyclerView1.scrollBy(dx, dy)
////                recyclerView1.addOnScrollListener(
////                    scrollListeners[0]!!
////                )
//            }
//        }
////        recyclerView1.addOnScrollListener(createScrollListener(recyclerView2))
////        recyclerView2.addOnScrollListener(createScrollListener(recyclerView1))
//
//    }
    private fun loadProgramGuide(
        isScrolled: Boolean = false,
        currentPrograms: MutableList<ChannelEpgDTO>? = null
    ) {

        val currentProgram = currentPrograms?.get(0)

        currentPrograms?.remove(currentProgram)
        if (!isScrolled) {
            channelList = currentPrograms
            channelContent = currentPrograms?.map { it.VP }
            setUpChannels(currentPrograms)
        } else
            channelListNext = currentPrograms

        setUpChannels(currentPrograms)

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
//                    channelIndex = channelListNext?.indexOf(program) ?: 0
                } else {
//                    channelIndex = channelList?.indexOf(program) ?: 0
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
//                    channelIndex = channelListNext?.indexOf(program) ?: 0
                } else {
//                    channelIndex = channelList?.indexOf(program) ?: 0
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

    private fun setUpChannels(channelList: List<ChannelEpgDTO>?) {
//        val adapter = DisconnectedChannelAdapter(
//            onChannelFocused = ::playChannelVideoBg,
//            onChannelClicked = ::launchExoPlayer
//        )
//        adapter.setChannelList(channelList)
//        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.layoutManager =
//            LinearLayoutManager(this)
//        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.adapter = adapter

        val adapter = DisChannelAdapter(onChannelClicked = ::launchExoPlayer)
        adapter.setChannelList(channelList)
        binding.rvProgramGuide.layoutManager = LinearLayoutManager(this)
        binding.rvProgramGuide.adapter = adapter


    }

}