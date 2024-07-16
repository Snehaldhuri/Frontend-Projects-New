package com.diipl.moviebeam.ui.programguide

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.data.dto.program.ChannelListResponse
import com.diipl.moviebeam.data.dto.remote.FrequencyModel
import com.diipl.moviebeam.databinding.ActivityDisconnectedPrgBinding
import com.diipl.moviebeam.databinding.DialogSearchProgramBinding
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
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
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

    private var irService: IIrService? = null

    private val usbManager: UsbManager by lazy { getSystemService(USB_SERVICE) as UsbManager }
    private lateinit var usbDevice: UsbDevice
    private var hotelChannelVideo =""
    private var hotelChannelNo =""
    private var hotelChannelName =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSet()
        programGuideViewModel.getAccountSetupResponseData(accountSetupDataStore)
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
        fetchDetails()
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
            super.onBackPressed()
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