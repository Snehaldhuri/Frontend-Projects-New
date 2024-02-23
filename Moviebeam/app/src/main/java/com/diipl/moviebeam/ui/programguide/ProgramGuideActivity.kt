package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.HotelChannel
import com.diipl.moviebeam.data.dto.program.ProgramDTO
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityProgramGuideBinding
import com.diipl.moviebeam.databinding.DialogSearchProgramBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showKeyboard
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "ProgramGuideActivity"

@AndroidEntryPoint
class ProgramGuideActivity : BaseActivity() {

    private lateinit var binding: ActivityProgramGuideBinding
    private val programGuideViewModel: ProgramGuideViewModel by viewModels()

    private var gradient: GradientDrawable? = null
    private var channelContent: List<String> = emptyList()
    private var channelList: List<ProgramDTO> = emptyList()
    private var cNo = 0
    private var isFScreenExit = false
    private var isSearched = false
    private lateinit var hotelChannel: HotelChannel

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    override fun observeViewModel() {
        observe(programGuideViewModel.weatherLiveData, ::handleWeatherResponse)
        observeSnackBarMessages(programGuideViewModel.showSnackBar)
        observeToast(programGuideViewModel.showToast)
    }

    override fun initViewBinding() {
        binding = ActivityProgramGuideBinding.inflate(layoutInflater)
        fetchDetails()
        fetchDataFromDatastore()
        setContentView(binding.root)
        binding.btnBack.setOnFocusChangeListener(::handleBtnFocus)
        binding.btnSearch.setOnFocusChangeListener(::handleBtnFocus)
        binding.btnBack.setOnClickListener { finish() }
        parseData("")
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.post {
            binding.layoutProgramGuide.layoutPrgGuide.rvChannel.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
        }
        LoggingService.sendMessageToWebSocket("In ProgramGuidePage activity")

    }

    /*
        override fun onBackPressed() {
            if (isSearched){
                parseData("")
            } else {
                super.onBackPressed()
                finish()
            }
        }
    */

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: ")
    }


    override fun onResume() {
        super.onResume()
        if (isFScreenExit) {
            playChannelVideoBg(null)
        }

        binding.btnSearch.setOnKeyListener { view, code, keyEvent ->
            when (code) {
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    if (view.isFocused) {
                        showSearchDialog()
                        view.clearFocus()
                    }
                }
            }
            false
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
        dialogBinding.etSearch.background = getGradient(
            intent.extras?.getString(Constants.GRADIENT_START_COLOR_PARAM),
            intent.extras?.getString(Constants.GRADIENT_END_COLOR_PARAM)
        )

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
        list.forEachIndexed { index, model ->
            if (name.isNotEmpty()) {
                if (model.CN.contains(name, true) or (model.CNO.toString().contains(name, true))) {
                    focusIndex = index
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

        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        intent.extras?.let {
            binding.layoutHeader.tvTitle.text = it.getString(Constants.TITLE_PARAM)
            gradient =
                getGradient(
                    it.getString(Constants.GRADIENT_START_COLOR_PARAM),
                    it.getString(Constants.GRADIENT_END_COLOR_PARAM)
                )
            loadBg(it.getString("themeBackgroundFileName"))
        }
    }

    private fun fetchDataFromDatastore() {
        programGuideViewModel.getWeatherResponseData(weatherDataStore)
    }

    private fun getGradient(
        gradientStartColor: String?,
        gradientEndColor: String?
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
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

    private fun handleBtnFocus(view: View, focus: Boolean) {
        if (focus) {
            view.background = gradient
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }

    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text =
                    programGuideViewModel.weatherLiveData.value?.data?.tempCondition
                programGuideViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud?.let {
                    binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather.loadImagesWithGlideExt(
                        it
                    )
                }
            }

            else -> {
                status.errorCode?.let { programGuideViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun readJson(): HashMap<*, *> {
        val gson = Gson()
        val i: InputStream = this.assets.open("ProgramGuideData.json")
        val br = BufferedReader(InputStreamReader(i))
        return gson.fromJson(br, HashMap::class.java)
    }

    private fun fetchCurrentProgramDetails(): String {
        val cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR)
        val minutes = cal.get(Calendar.MINUTE)
        val amPm = cal.get(Calendar.AM_PM)
        val time = StringBuffer("")

        if (amPm == 0) time.append("am")
        else time.append("pm")

        if (hour == 0) hour = 12

        if (hour < 10) time.append(appendZeros(hour))
        else time.append(hour.toString())

        if (minutes < 30) time.append("00")
        else time.append("30")

        return time.toString()
    }

    private fun appendZeros(value: Int): String {
        val str = StringBuffer(value.toString()).reverse()
        str.append("0")
        return str.reverse().toString()
    }

    private fun parseData(text: String) {
        val programs = readJson()
        val key = fetchCurrentProgramDetails()
        val current = programs[key]
        val data = mapToDto(current as List<*>)
        val currentPrograms = data.toMutableList()
        /* val currentPrograms = mutableListOf<ProgramDTO>()
         if (text.isNotEmpty()){
            for (model in data){
                if (model.CN.lowercase().contains(text.lowercase()) or (model.CNO.toString().lowercase() == text.lowercase())){
                    currentPrograms.add(model)
                }
            }
             isSearched = if (currentPrograms.isEmpty()){
                 currentPrograms.addAll(data)
                 programGuideViewModel.showToastMessage("No such channel with $text")
                 false
             } else true

         } else {
             currentPrograms.addAll(data)
             isSearched = false
         }*/

        val currentProgram = data[0]
        binding.layoutProgramGuide.tvTime1.text = currentProgram.P1_DST
        binding.layoutProgramGuide.tvTime2.text = currentProgram.P2_DST
        binding.layoutProgramGuide.tvTime3.text = currentProgram.P3_DST
        binding.layoutProgramGuide.tvTime4.text = currentProgram.P4_DST

        currentPrograms.remove(currentProgram)
        this.channelList = currentPrograms
        this.channelContent = currentPrograms.map { it.VP }
        setUpChannels(currentPrograms)
        setUpPrograms(currentPrograms, currentProgram.P4_DST)
    }

    private fun mapToDto(list: List<*>): MutableList<ProgramDTO> {
        val gson = Gson()
        val fooType: Type = object : TypeToken<Map<String, Any>?>() {}.type
        return list.map {
            gson.fromJson(gson.toJson(it, fooType), ProgramDTO::class.java)
        }.toMutableList()
    }

    private fun setUpChannels(channelList: List<ProgramDTO>) {
        val adapter = ChannelAdapter(
            onChannelFocused = ::playChannelVideoBg,
            onChannelClicked = ::launchExoPlayer
        )
        adapter.setChannelList(channelList)
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.layoutManager =
            LinearLayoutManager(this)
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.adapter = adapter
    }

    private fun setUpPrograms(programsList: List<ProgramDTO>, p4Dst: String) {
        val adapter = ProgramsAdapter(
            onProgramFocused = ::playChannelVideoBg,
            onProgramClicked = ::launchExoPlayer
        )
        adapter.setProgramList(programsList)
        adapter.setProg4Dst(p4Dst)
        binding.layoutProgramGuide.layoutPrgGuide.rvProgram.layoutManager =
            LinearLayoutManager(this)
        binding.layoutProgramGuide.layoutPrgGuide.rvProgram.adapter = adapter
    }

    private fun playChannelVideoBg(program: ProgramDTO?) {
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            if (this.cNo != program.CNO) {
                initializePlayer(program)
                binding.layoutVideo.root.toVisible()
                binding.tvProgramTitle.text = program.P1_PT
                binding.tvDescription.text = program.P1_SY
                this.cNo = program.CNO
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializePlayer(program: ProgramDTO) {
        val player = ExoPlayer.Builder(this)
            .setRenderersFactory(DefaultRenderersFactory(this).setEnableDecoderFallback(true))
            .build()
        val playerView = binding.layoutVideo.videoView
        playerView.player?.release()
        playerView.player = player
        player?.let {
            it.setMediaItem(MediaItem.fromUri(program.VP))
            it.playWhenReady = true
            it.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            it.prepare()
            it.play()
        }

    }

    private fun launchExoPlayer(program: ProgramDTO) {
        binding.layoutVideo.videoView.player?.pause()
        val bundle = Bundle()
        bundle.putStringArrayList(
            Constants.CONTENT_LIST_PARAM,
            channelContent as ArrayList<String>
        )
        bundle.putInt(Constants.SELECTED_CHANNEL_INDEX, channelList.indexOf(program))
        bundle.putInt(Constants.CHANEL_NO_PARAM, program.CNO)
        bundle.putString(Constants.CHANNEL_NAME_PARAM, program.CN)
        bundle.putString(Constants.CHANNEL_LOGO_PARAM, program.CL)
        bundle.putString(Constants.NOW_SHOWING_PARAM, program.liveProg1)
        bundle.putString(Constants.NEXT_PROGRAM_PARAM, program.liveProg2)
        bundle.putString(Constants.PROG_1_TIME_PARAM, program.prog1Time)
        bundle.putString(Constants.PROG_2_TIME_PARAM, program.prog2Time)
        bundle.putString(Constants.CHANNEL_LIST_PARAM, Gson().toJson(channelList))

        val intent = Intent(this, PrgGuidePlayerActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        this.isFScreenExit = true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.layoutVideo.videoView.player?.release()
        Log.e(TAG, "onDestroy: ")
    }

}
