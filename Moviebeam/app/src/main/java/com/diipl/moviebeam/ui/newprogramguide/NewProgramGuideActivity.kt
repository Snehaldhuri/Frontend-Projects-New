package com.diipl.moviebeam.ui.newprogramguide

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.tv.TvContract
import android.media.tv.TvInputManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.HotelChannel
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.DvbChannel
import com.diipl.moviebeam.data.dto.remote.BTCommandModel
import com.diipl.moviebeam.data.dto.remote.IRFrequencyModel
import com.diipl.moviebeam.databinding.ActivityNewProgramGuideBinding
import com.diipl.moviebeam.databinding.DialogSearchProgramBinding
import com.diipl.moviebeam.service.remote.BTService
import com.diipl.moviebeam.service.remote.IIrService
import com.diipl.moviebeam.service.remote.UsbIrService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.player.LiveTVActivity
import com.diipl.moviebeam.ui.player.LiveTVActivity.Companion
import com.diipl.moviebeam.ui.player.LiveTVActivity.Companion.mChannelList
import com.diipl.moviebeam.ui.player.PlayerActivity
import com.diipl.moviebeam.ui.programguide.ProgramGuideViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.DTV_INPUT_ID
import com.diipl.moviebeam.utils.Constants.DTV_KIT_PACKAGE_NAME
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.fetchCurrentProgramKey
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showKeyboard
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
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

private const val TAG = "NewProgramGuideActivity"

@AndroidEntryPoint
class NewProgramGuideActivity : BaseActivity() {

    private lateinit var binding: ActivityNewProgramGuideBinding
    private val programGuideViewModel: ProgramGuideViewModel by viewModels()

    private var isFScreenExit = false

    private lateinit var hotelChannel: HotelChannel
    private var hotelChannelVideo: String = ""

    private var channelNumberInput = ""
    private val channelSearchDelay = 1000L // Delay in milliseconds
    private val handler = Handler(Looper.getMainLooper())
    private val channelSearchRunnable = Runnable {
        if (channelNumberInput.isNotEmpty()) {
            searchInPage(channelNumberInput)
        }
    }


    @Inject
    lateinit var preferences: SharedPreference

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    private lateinit var adapter: ProgramGuideAdapter

    private var focusedPosition: Int = 0
    private var programGuideList = mutableListOf<ChannelEpgDTO>()
    private var key: String? = fetchCurrentProgramKey()
    private lateinit var programDateTime: ChannelEpgDTO

    private var irService: IIrService? = null
    private lateinit var btService: BTService
    private var switchedToTV = false

    private var broadCastType = ""

    override fun observeViewModel() {
        observe(programGuideViewModel.accountSetupLiveData, ::handleAccountSetupResponse)

        observeSnackBarMessages(programGuideViewModel.showSnackBar)
        observeToast(programGuideViewModel.showToast)
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let { response ->
                    hotelChannel = response.hotelChannelList[0]
                    hotelChannelVideo = response.httpStreamingHotelvideoUrl + hotelChannel.fileName
                    broadCastType = response.tvBroadcastType
                    this.getChannelsFromRoomDB()
                }
            }
            else -> {
            }
        }
    }

    override fun onStart() {
        super.onStart()

        programGuideViewModel.getAccountSetupResponseData(accountSetupDataStore)

        adapter = ProgramGuideAdapter(
            onChannelFocused = ::playChannelVideoBg,
            updateProgramAndChannelData = ::onProgramFocused,
            loadNewPrograms = ::loadNewPrograms,
            onChannelClicked = ::launchExoPlayer
        )
        setAdapter()

    }

    override fun initViewBinding() {
        binding = ActivityNewProgramGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        binding.btnBack.handleFocusChange()
        binding.btnSearch.handleFocusChange()
        binding.btnBack.setOnClickListener { handleBackRemoteClick() }

        binding.pbLoader.toVisible()
    }

    private fun setAdapter() {
        adapter.setProgramList(programGuideList)

        binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.setHasFixedSize(true)
        binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_CHROMECAST) {
            if (preferences.isIRRemote)
                irService = UsbIrService(this).also { it.getInstance() }
            else
                btService = BTService(this, lifecycle).also { it.findBondedDevice() }
        }

    }

    private fun loadNewPrograms(isNextOrPrevious: Int, position: Int) {
        if (!checkNextProgramTimeSlotExists(isNextOrPrevious)) {
            return
        }
        focusedPosition = position
        updateKey(isNextOrPrevious)
    }

    private fun checkNextProgramTimeSlotExists(nextOrPrevious: Int): Boolean {
        return if (nextOrPrevious == 1) {
            //check for previous slot exists or not
            checkPreviousSlotExistsOrNot()
        } else {
            checkFutureSlotExistsOrNot()
        }
    }

    private fun checkPreviousSlotExistsOrNot(): Boolean {
        val startDateTime = convertProgramStartOrEndTime(programDateTime.P1_ST)
        // Create a Calendar object with the current time
        val calendar = Calendar.getInstance()
        return startDateTime.after(calendar.time)
    }

    private fun convertProgramStartOrEndTime(time: String?): Date {
        var date: Date? = null
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())

        try {
            // Parse the string to a Date object
            date = time?.let { dateFormat.parse(it) }!!

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date!!
    }

    private fun checkFutureSlotExistsOrNot(): Boolean {
        val endDateTime = convertProgramStartOrEndTime(programDateTime.P4_ET)

        // Create a Calendar object with the current time
        val epgEndTime: Date = convertProgramStartOrEndTime(preferenceHandler.epgEndTime)

        return endDateTime.compareTo(epgEndTime) == -1 //a value less than 0 if this Date is before the Date argument.
    }

    private fun updateKey(isNextOrPrevious: Int) {
        //1 for previous
        //2 for next
        val dateFormatter = SimpleDateFormat("ddMMyyyyhhmma", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = key?.let { dateFormatter.parse(it) }!!
        if (isNextOrPrevious == 1) {
            cal.add(Calendar.HOUR_OF_DAY, -2)
        } else {
            cal.add(Calendar.HOUR_OF_DAY, 2)
        }
        key = fetchCurrentProgramKey(cal)
        updateChannels()
    }

    private fun updateChannels() {
        programGuideViewModel.getAllChannels(key).observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                programGuideList.clear()
                loadProgramGuide(data)
                adapter.setProgramList(programGuideList)
                // focus to adapter position
                adapter.updateProgramFocus(focusedPosition)

                binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.scrollToPosition(
                    focusedPosition
                )
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (isFScreenExit) {
            playChannelVideoBg(null)
        }

        lifecycleScope.launch {
            delay(1000)
            binding.root.loadBg()
            binding.layoutHeader.ivHotelLogo.loadLogo()
        }

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

    }

    private fun getChannelsFromRoomDB() {
        programGuideViewModel.getAllChannels(key).observe(this) { data ->
            if (!data.isNullOrEmpty()) {
                programGuideList.clear()

                loadProgramGuide(data)
                adapter.setProgramList(programGuideList)

                binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.post {
                    binding.cvProgramGuide.toVisible()
                    binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.postDelayed({
                        val viewHolder = binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.findViewHolderForAdapterPosition(focusedPosition)
                        viewHolder?.itemView?.requestFocus()
                    }, 100)
                }
                }
                //setAdapter()
                adapter.notifyDataSetChanged()
            } else {
                programGuideViewModel.showToastMessage(getString(R.string.please_contact_the_front_desk_for_assistance))
            }
            binding.pbLoader.toInvisible()
        }
    }

    fun showSearchDialog() {
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
                }
            }
            false
        }
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_3,
            KeyEvent.KEYCODE_4,
            KeyEvent.KEYCODE_5,
            KeyEvent.KEYCODE_6,
            KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_8,
            KeyEvent.KEYCODE_9 -> {
                // Append the key pressed to the channel number
                channelNumberInput += (keyCode - KeyEvent.KEYCODE_0).toString()

                // Limit the input to 4 digits
                if (channelNumberInput.length > 4) {
                    channelNumberInput = channelNumberInput.takeLast(4)
                }

                binding.cardNumber.toVisible()
                binding.tvNumber.text = channelNumberInput

                // Reset the search delay
                handler.removeCallbacks(channelSearchRunnable)
                handler.postDelayed(channelSearchRunnable, channelSearchDelay)

                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun searchInAdapter(name: String) {
        var focusIndex = -1
        run breaking@{
            programGuideList?.forEachIndexed { index, model ->
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

        binding.cardNumber.toGone()

        if (focusIndex >= 0) {
            binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.scrollToPosition(focusIndex)
        } else programGuideViewModel.showToastMessage("No such channel with $name")

        //focus on channel with searched value
        adapter.updateFocusOnSearch(focusIndex)
    }

    private fun searchInPage(name: String) {
        var focusIndex = -1
        val originalNum = name.trimStart('0')
        Log.e(TAG, "Switching to channel: $name ->  $originalNum")

        run breaking@{
            programGuideList?.forEachIndexed { index, model ->
                if (name.isNotEmpty()) {
                    if ((model.CNO.toString() == originalNum) ||
                        (model.CN?.equals(originalNum, ignoreCase = true) == true)) {
                        focusIndex = index
                        return@breaking
                    }
                }
            }
        }

        binding.cardNumber.toGone()

        if (focusIndex >= 0) {
            binding.layoutProgramGuide.layoutPrgGuide.rvProgramGuideEpg.scrollToPosition(focusIndex)
            adapter.updateFocusOnSearch(focusIndex)

        } else {
            programGuideViewModel.showToastMessage("No such channel with $name")
        }

        // Reset the input after search
        channelNumberInput = ""
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
        Log.d(TAG, "playChannelVideoBg: Snehal")
        updatePopupText(program, binding.tvPopupProgText)
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            updateTvProgramTitleAndDescription(program.P1_PT, program.P1_SY)
        }
    }

    private fun updateTvProgramTitleAndDescription(title: String?, synopsis: String?) {
        binding.tvProgramTitle.text = title
        binding.tvDescription.text = synopsis
    }

    private fun onProgramFocused(program: ChannelEpgDTO?, title: String?, synopsis: String?) {
        updatePopupText(program, binding.tvPopupProgText)
        if (program == null) {
            isFScreenExit = false
            binding.layoutVideo.videoView.player?.play()
        } else {
            updateTvProgramTitleAndDescription(title, synopsis)
        }
    }

    private fun updatePopupText(channel: ChannelEpgDTO?, tvPopupText: TextView) {
        Log.d(TAG, "updatePopupText: Snehal popup")
        tvPopupText.text = "Please press the OK button on your remote to tune in to ${channel?.CN}."
        binding.tvPopupProgDesc.text = createSpannableString()
        binding.tvPopupProgDesc.textAlignment = View.TEXT_ALIGNMENT_CENTER
    }

    private fun createSpannableString(): SpannableString {
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
        return spannableString
    }

    override fun onStop() {
        super.onStop()
        if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_CHROMECAST)
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.layoutVideo.videoView.player?.release()
    }

    private fun loadProgramGuide(
        currentPrograms: MutableList<ChannelEpgDTO>? = null,
    ) {
        val currentProgram = currentPrograms?.get(0)
        binding.layoutProgramGuide.tvTime1.text = currentProgram?.P1_DST
        binding.layoutProgramGuide.tvTime2.text = currentProgram?.P2_DST
        binding.layoutProgramGuide.tvTime3.text = currentProgram?.P3_DST
        binding.layoutProgramGuide.tvTime4.text = currentProgram?.P4_DST

        setEpgDate(binding.layoutProgramGuide.tvDate, currentProgram?.P1_ST)

        if (currentProgram != null) {
            programDateTime = currentProgram
        }

        currentPrograms?.remove(currentProgram)

        val param1 =
            if (hotelChannel.tvChannelBroadcastType == IP_BROADCAST_TYPE) hotelChannel.ip else if (hotelChannel.tvChannelBroadcastType == RF_BROADCAST_TYPE) hotelChannel.major else ""
        val param2 =
            if (hotelChannel.tvChannelBroadcastType == IP_BROADCAST_TYPE) hotelChannel.port else if (hotelChannel.tvChannelBroadcastType == RF_BROADCAST_TYPE) hotelChannel.minor else ""

        Log.e(TAG, "loadProgramGuide: ${hotelChannel.tvChannelBroadcastType}, $broadCastType,  $param1, $param2")

        val hotelVideoProgram = ChannelEpgDTO(
            CN = hotelChannel.channelName,
            VP = hotelChannelVideo,
            CNO = hotelChannel.channelNo,
            param1 = param1,
            param2 = param2,
            P1_PT = hotelChannel.channelName,
            P1_CLS = "80",
            C = "1"
        )
        currentPrograms?.add(0, hotelVideoProgram)
        if (currentPrograms != null) {
            programGuideList.addAll(currentPrograms)
        }
        CURRENT_PROGRAMS = currentPrograms

    }

    private fun setEpgDate(epgDate: TextClock, p1St: String?) {
        val startTime: Date = convertProgramStartOrEndTime(p1St)

        val formattedDate = SimpleDateFormat("MMM dd, yyyy").format(startTime)
        epgDate.text = formattedDate
    }

    private fun launchExoPlayer(program: ChannelEpgDTO?) {
        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_CHROMECAST -> {
                if (preferences.isIRRemote) switchToTV(program)
                else switchToTVWithBluetooth(program)
            }

            Constants.BUILD_TYPE_STB -> {
                Log.e(TAG, "launchExoPlayer: $broadCastType")
                if (broadCastType == IP_BROADCAST_TYPE)
                    tuneIPChannels(program)
                if (broadCastType == RF_BROADCAST_TYPE)
                    tuneChannels(program)
            }
        }
    }

    private fun switchToTV(program: ChannelEpgDTO?) {
        clearCache()
        lifecycleScope.launch {
            val model = preferences.irFrequencyModel
            irService?.let { service ->
                if (service.isConnected()) {
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
            if(::btService.isInitialized) {
                if (btService.isConnected()) {
                    val model = preferences.btCommandModel
                    btService.transmit(model.HDMI1)
                    switchedToTV = false
                } else showToast(BTService.MSG_BT_NOT_CONNECTED)
            }
        }
        Log.e(TAG, "switchToHDMI  $switchedToTV")
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
            val msg = "Unable to find any dvb channels, are channel searchable?"
            logE(msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return
        }

        mChannelList.sortBy { dvbChannel -> dvbChannel.number }
    }

    private fun tuneIPChannels(program: ChannelEpgDTO?) {
        PlayerActivity.programGuideList.addAll(programGuideList)
        if (PlayerActivity.programGuideList[0].param1?.isEmpty() == true) {
            PlayerActivity.programGuideList.removeAt(0)
        }

        val pos = PlayerActivity.programGuideList.indexOf(program)
        focusedPosition = programGuideList.indexOf(program)
        onPause = true

        val intent = Intent(applicationContext, PlayerActivity::class.java)
        intent.putExtra("currentPos", pos)
        startActivity(intent)
    }

    private fun tuneChannels(program: ChannelEpgDTO?) {
        LiveTVActivity.programGuideList.addAll(programGuideList)
        if (LiveTVActivity.programGuideList[0].param1?.isEmpty() == true) {
            LiveTVActivity.programGuideList.removeAt(0)
        }

        val pos = LiveTVActivity.programGuideList.indexOf(program)
        focusedPosition = programGuideList.indexOf(program)
        onPause = true
        val intent = Intent(applicationContext, LiveTVActivity::class.java)
        intent.putExtra("currentPos", pos)
        startActivity(intent)
    }

    private fun findDvbInput(): String? {
        val mTvInputManager = getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager

        logD("============================================")
        logD("enumerate tv input")
        var dvbInputFound = false
        var dtvInputComponent = ""
        mTvInputManager.let {
            for (tvInputInfo in it.tvInputList) {
                if (tvInputInfo.id.startsWith("$DTV_KIT_PACKAGE_NAME/")) {
                    dvbInputFound = true
                    dtvInputComponent = tvInputInfo.id
                }
            }
        }

        logD("============================================")

        if (!dvbInputFound) {
            val msg = "Failed to find dvb input"
            logE(msg)
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            return null
        }

        return dtvInputComponent
    }

    private fun fetchTVChannels() {
        if (hasReadTvListings(this)) {
            loadChannelList()
        } else {
            grantPermission()
        }
    }

    private fun grantPermission() {
        try {
            Intent(Intent.ACTION_VIEW).apply {
                component =
                    ComponentName(Constants.MDM_PACKAGE_NAME, Constants.MDM_GRANT_PERMISSION)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(this)
            }
            lifecycleScope.launch {
                delay(1000)
                fetchTVChannels()
            }
        } catch (e: Exception) {
            showToast("Please grant READ_TV_LISTINGS permission!")
            logE("grantPermission: Exception ->  ${e.localizedMessage}")
        }
    }

    override fun onPause() {
        super.onPause()
        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_STB -> {

            }

            else -> finish()
        }

    }

    fun handleBackRemoteClick() {
        onBackPressed()
    }

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

    companion object {
        var CURRENT_PROGRAMS: List<ChannelEpgDTO>? = null
        var onPause = false

        const val IP_BROADCAST_TYPE = "IP"
        const val RF_BROADCAST_TYPE = "RF"

    }

}