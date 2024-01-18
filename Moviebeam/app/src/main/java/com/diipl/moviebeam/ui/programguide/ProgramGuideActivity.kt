package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.program.ProgramDTO
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityProgramGuideBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class ProgramGuideActivity : BaseActivity() {

    private lateinit var binding: ActivityProgramGuideBinding
    private val programGuideViewModel: ProgramGuideViewModel by viewModels()

    private var gradient: GradientDrawable? = null
    private var channelContent: List<String> = emptyList()
    private var channelList: List<ProgramDTO> = emptyList()
    private var cNo = 0
    private var isFScreenExit = false

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    override fun observeViewModel() {
        observe(programGuideViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(programGuideViewModel.dateTimeLiveData, ::handleDateTimeResponse)
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
        parseData()
        binding.layoutProgramGuide.layoutPrgGuide.rvChannel.post {
            binding.layoutProgramGuide.layoutPrgGuide.rvChannel.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFScreenExit) {
            playChannelVideoBg(null)
        }
    }

    private fun fetchDetails() {
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExt(it)
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
                    replaceDegreeSymbol(programGuideViewModel.weatherLiveData.value?.data?.tempCondition)
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

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                programGuideViewModel.dateTimeLiveData.value?.data?.let {
                    binding.layoutHeader.layoutWeatherTime.tvDate.text = it.date
                    binding.layoutHeader.layoutWeatherTime.tvTime.text = it.time
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { programGuideViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun replaceDegreeSymbol(temp: String?): String {
        var temperature = ""
        temp?.let {
            temperature = if (it.contains("&deg C")) {
                it.replace("&deg C", Constants.SYMBOL_DEGREE_CELSIUS)
            } else {
                it.replace("&deg F", Constants.SYMBOL_DEGREE_FAHRENHEIT)
            }
        }
        return temperature
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

    @SuppressLint("NewApi")
    private fun parseData() {
        val programs = readJson()
        val key = fetchCurrentProgramDetails()
        val current = programs[key]
        val currentPrograms = mapToDto(current as List<*>)

        val currentProgram = currentPrograms[0]
        binding.layoutProgramGuide.tvTime1.text = currentProgram.P1_DST
        binding.layoutProgramGuide.tvTime2.text = currentProgram.P2_DST
        binding.layoutProgramGuide.tvTime3.text = currentProgram.P3_DST
        binding.layoutProgramGuide.tvTime4.text = currentProgram.P4_DST
        val format = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        binding.layoutProgramGuide.tvDate.text = LocalDate.now().format(format)

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
            binding.layoutVideo.videoView.start()
            isFScreenExit = false
        } else {
            if (this.cNo != program.CNO) {
                val videoView = binding.layoutVideo.videoView
                val uri = Uri.parse(program.VP)
                videoView.setVideoURI(uri)
                binding.layoutVideo.root.toVisible()
                videoView.start()
                binding.tvProgramTitle.text = program.P1_PT
                binding.tvDescription.text = program.P1_SY
                this.cNo = program.CNO

            }
        }
    }

    private fun launchExoPlayer(program: ProgramDTO) {
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

}