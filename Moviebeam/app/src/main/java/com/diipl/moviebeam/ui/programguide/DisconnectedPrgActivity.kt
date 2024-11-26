package com.diipl.moviebeam.ui.programguide

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.remote.BTCommandModel
import com.diipl.moviebeam.data.dto.remote.IRFrequencyModel
import com.diipl.moviebeam.databinding.ActivityDisconnectedPrgBinding
import com.diipl.moviebeam.databinding.DialogSearchProgramBinding
import com.diipl.moviebeam.service.remote.BTService
import com.diipl.moviebeam.service.remote.IIrService
import com.diipl.moviebeam.service.remote.UsbIrService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showKeyboard
import com.diipl.moviebeam.utils.showToast
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

    private var currentSearchQuery: String = ""
    private var isSearchDialogOpen: Boolean = false


    @Inject
    lateinit var preferences : SharedPreference

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    @Inject
    lateinit var channelListDataStore: DataStore<ChannelListResponse>

    private var hotelChannelVideo =""
    private var hotelChannelNo =""
    private var hotelChannelName =""

    private var irService: IIrService? = null
    private lateinit var btService: BTService
    private var switchedToTV = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        programGuideViewModel.getAccountSetupResponseData(accountSetupDataStore)

        if (preferences.isIRRemote)
            irService = UsbIrService(this).also { it.getInstance() }
        else
            btService = BTService(this, lifecycle).also { it.findBondedDevice() }

    }

    override fun observeViewModel() {
        observe(programGuideViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observe(programGuideViewModel.channelListLiveData, ::handleChannelListResponse)
        observeSnackBarMessages(programGuideViewModel.showSnackBar)
        observeToast(programGuideViewModel.showToast)
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Success -> {
                status.data?.let { response ->
                     hotelChannelVideo = response.httpStreamingHotelvideoUrl + response.hotelChannelList[0].fileName
                     hotelChannelNo = response.hotelChannelList[0].channelNo
                     hotelChannelName = response.hotelChannelList[0].channelName

                    programGuideViewModel.getChannelListResponseData(channelListDataStore)
                }
            }
            else -> {
                status.errorCode?.let { programGuideViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { programGuideViewModel.showToastMessage(it) }
            }
        }
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
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        setContentView(binding.root)
        binding.btnBack.handleFocusChange()
        binding.btnSearch.handleFocusChange()
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
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

    private fun showSearchDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogSearchProgramBinding.inflate(LayoutInflater.from(applicationContext))
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.etSearch.setText(currentSearchQuery)
        dialog.show()

        isSearchDialogOpen = true
        if (isSearchDialogOpen) {
            dialog.setOnKeyListener { _, keycode, _ ->
                when (keycode) {
                    KeyEvent.KEYCODE_BACK -> {
                        currentSearchQuery = ""
                        searchInAdapter(currentSearchQuery)
                        binding.btnSearch.text = "Search"
                        isSearchDialogOpen = false
                        val dialog = supportFragmentManager.findFragmentByTag("searchDialog") as? AlertDialog
                        dialog?.dismiss()
                    }
                }
                false
            }

        }

        dialogBinding.etSearch.requestFocus()
        dialogBinding.etSearch.showKeyboard()
        dialogBinding.etSearch.handleFocusChange()

        dialog.setOnDismissListener {
            isSearchDialogOpen = false
            dialog.dismiss()
        }

        dialogBinding.etSearch.setOnEditorActionListener { textView, id, keyEvent ->
            when (id) {
                EditorInfo.IME_ACTION_DONE -> {
                    dialog.dismiss()
                    dialogBinding.etSearch.hideKeyboard()
                    searchInAdapter(textView.text.toString().trim())
                    binding.rvProgramGuide.requestFocus()
                    val searchTerm = textView.text.toString().trim()

                    currentSearchQuery = searchTerm
                    Log.d(TAG, "showSearchDialog: $currentSearchQuery")
                    if(currentSearchQuery != ""){
                        dialogBinding.etSearch.setText(currentSearchQuery)
                    }
                    binding.btnSearch.text = if (currentSearchQuery.isEmpty()) "Search" else currentSearchQuery
                }
            }
            false
        }

        dialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInAdapter(s.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    fun handleBackRemoteClick() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (isSearchDialogOpen) {
            currentSearchQuery = ""
            searchInAdapter(currentSearchQuery)
            binding.btnSearch.text = "Search"
            isSearchDialogOpen = false // Ensure the flag is reset
            val dialog = supportFragmentManager.findFragmentByTag("searchDialog") as? AlertDialog
            dialog?.dismiss()
        } else if (currentSearchQuery.isNotEmpty()) {
            currentSearchQuery = ""
            searchInAdapter(currentSearchQuery)
            binding.btnSearch.text = "Search"
        } else {
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
    }


    private fun searchInAdapter(name: String) {
//        val adapter = binding.rvProgramGuide.adapter as DisChannelAdapter
//        val list = adapter.getChannelList()
//        var focusIndex = -1
//        run breaking@{
//            list?.forEachIndexed { index, model ->
//                if (name.isNotEmpty()) {
//                    if ((model.CN?.contains(name, true) == true) or (model.CNO.toString()
//                            .contains(name, true))
//                    ) {
//                        focusIndex = index
//                        return@breaking
//                    }
//                }
//            }
//        }
//
//        if (focusIndex >= 0) {
//            binding.rvProgramGuide.scrollToPosition(focusIndex)
//        } else programGuideViewModel.showToastMessage("No such channel with $name")
//        adapter.updateFocus(focusIndex)
        val adapter = binding.rvProgramGuide.adapter as DisChannelAdapter
        adapter.filter(name)

        if (adapter.itemCount == 0) {
            programGuideViewModel.showToastMessage("No channel found with \"$name\"")
        }

    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    override fun onStop() {
        super.onStop()
        if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_CHROMECAST)
            finish()
    }


    private fun launchExoPlayer(program: ChannelEpgDTO?) {
        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_CHROMECAST -> {
                if (preferences.isIRRemote) switchToTV(program)
                else switchToTVWithBluetooth(program)
            }
            Constants.BUILD_TYPE_STB -> {
//                tuneChannels(program)
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

    override fun onDestroy() {
        super.onDestroy()
        binding.layoutVideo.videoView.player?.release()
        Log.e(TAG, "onDestroy: ")
    }

    private fun setUpChannels(channelList: MutableList<ChannelEpgDTO>?) {

        val adapter = DisChannelAdapter(onChannelClicked = ::launchExoPlayer)

        if (channelList != null && channelList.isNotEmpty()) {

            val updatedChannelList = mutableListOf<ChannelEpgDTO>().apply {
                addAll(channelList)
            }

            val hotelVideoProgram = ChannelEpgDTO(
                CN = hotelChannelName,
                VP = hotelChannelVideo,
                CNO = hotelChannelNo,
            )

            updatedChannelList.add(0, hotelVideoProgram)

            adapter.setChannelList(updatedChannelList)
        }

        binding.rvProgramGuide.layoutManager = GridLayoutManager(this, 4)
        binding.rvProgramGuide.adapter = adapter
    }


}