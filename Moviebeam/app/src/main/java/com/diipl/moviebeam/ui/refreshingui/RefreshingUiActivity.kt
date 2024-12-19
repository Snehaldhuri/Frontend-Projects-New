package com.diipl.moviebeam.ui.refreshingui

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.data.dto.accountsetup.Buttons
import com.diipl.moviebeam.data.dto.kaping.KapingResponse
import com.diipl.moviebeam.databinding.ActivityRefreshingUiBinding
import com.diipl.moviebeam.service.handler.EPGHandler
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guest.message.GuestMessageActivity
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.stbdetail.APIViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.startActivity
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "RefreshingUiActivity"

@AndroidEntryPoint
class RefreshingUiActivity : BaseActivity() {

    private val viewModel: APIViewModel by viewModels()
    private lateinit var binding: ActivityRefreshingUiBinding
    private var networkJob: Job? = null
    private var isCancellable = true
    private var buttonsList: List<Buttons> = emptyList()
    private var isMenuMessageAvailable: Boolean = false
    private var kapingResponse: KapingResponse? = null

    override fun onBackPressed() {}

    override fun observeViewModel() {
        observe(viewModel.networkStatus, ::handleNetworkResponse)

        observeSnackBarMessages(viewModel.showSnackBar)
        observeToast(viewModel.showToast)
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_SHORT)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_SHORT)
    }

    override fun initViewBinding() {
        binding = ActivityRefreshingUiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        lifecycleScope.launch {
            val data = intent.getStringExtra("response")
            data?.let {
                kapingResponse = it.fromJson<KapingResponse>()

                buttonsList = accountDataStore.data.first().buttonsList
                buttonsList.forEach {
                    if (it.buttonName == Constants.MAIN_GUEST_MSG_ID) {
                        isMenuMessageAvailable = true
                    }
                }
                binding.btnOk.handleFocusChange()
                binding.btnOk.setOnClickListener {

                    val intent = if (isMenuMessageAvailable) {
                        Intent(applicationContext, GuestMessageActivity::class.java)
                    } else {
                        Intent(applicationContext, GuestServiceActivity::class.java).apply {
                            putExtra("btnId", Constants.MESSAGE_ID)
                        }
                    }
                    startActivity(intent)
                    finish()
                }
                binding.root.postDelayed({
                    handleKaping(kapingResponse)
                }, 5000)
            }

        }

    }

    private fun handleKaping(kapingResponse: KapingResponse?) {
        when (kapingResponse?.cmdData?.cmd) {
            KapingConstants.KAP_CMD_ACCOUNT_ACTIVATE -> {
                handleAccountActivateCmd()
            }

            KapingConstants.KAP_CMD_CHECK_IN -> {
                handleCheckInCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_CHECK_OUT -> {
                handleCheckOutCmd(kapingResponse)
            }

            KapingConstants.KAP_CMD_THEME_CHANGE -> {
                handleThemeChangeCmd()
            }

            KapingConstants.KAP_CMD_HS_CHANGE -> {
                handleHsChangeCmd()
            }

            KapingConstants.KAP_CMD_LA_CHANGE -> {
                handleLAChangeCmd()
            }

            KapingConstants.KAP_CMD_FETCH_SYNC_LIST -> {
                handleFetchSyncListCmd()
            }

            KapingConstants.KAP_CMD_FETCH_SHOWTIME_DATA -> {
                handleFetchShowtimeCmd()
            }

            KapingConstants.KAP_CMD_GET_CHANNEL_LIST -> {
                handleFetchChannelListCmd()
            }

            KapingConstants.KAP_CMD_GET_EPG_DATA -> {
                handleGetEPGDataCmd()
            }

            KapingConstants.KAP_CMD_GET_GUEST_MESSAGES -> {
                handleGuestMessageCmd()
            }
        }

    }

    private fun handleAccountActivateCmd() = lifecycleScope.launch {
        logD("Signalling to get account set up")
        val isComplete = viewModel.fetchAccountData(true)
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleCheckOutCmd(kapingResponse: KapingResponse) {
        logD("Signal for check out command")
        viewModel.updateGuestSession(false, kapingResponse.cmdData?.cmdData)
        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleCheckInCmd(kapingResponse: KapingResponse) {
        logD("Signal for check in command")
        viewModel.updateGuestSession(true, kapingResponse.cmdData?.cmdData)
        EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleThemeChangeCmd() = lifecycleScope.launch {
        val isComplete = viewModel.fetchThemeDetails()
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleHsChangeCmd() = lifecycleScope.launch {
        val isComplete = viewModel.fetchHotelService()
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleLAChangeCmd() = lifecycleScope.launch {
        val isComplete = viewModel.fetchLocalAttractions()
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleFetchSyncListCmd() = lifecycleScope.launch {
        val isComplete = viewModel.fetchMoviesMore()
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleFetchShowtimeCmd() = lifecycleScope.launch {
        val isComplete = viewModel.fetchShowtime()
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleFetchChannelListCmd() = lifecycleScope.launch {
        val isComplete = viewModel.fetchChannelList()
        if (isComplete)
            EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
        launchMain()
    }

    private fun handleGuestMessageCmd() = lifecycleScope.launch {
        if (preferenceHandler.isGuestCheckedIn) {
            val isComplete = viewModel.fetchGuestMessage()
            if (isComplete) {
                binding.tvTxt.toGone()
                binding.tvMsg.toGone()
                binding.tvInfo.toGone()

                binding.messageIv.toVisible()
                binding.btnOk.toVisible()
                binding.btnOk.requestFocus()
                EndlessService.kapingCmdExecutionResponse = KapingConstants.EXECUTED_SUCCESSFULLY
            }

        }
    }

    private fun handleGetEPGDataCmd() = lifecycleScope.launch {
        viewModel.fetchEpgData()
        viewModel.epgStatus.observe(this@RefreshingUiActivity) {
            when (it) {
                EPGHandler.STATUS_OK -> {
                    EndlessService.kapingCmdExecutionResponse =
                        KapingConstants.EXECUTED_SUCCESSFULLY
                    logD("In Get EPG data callback Success ")
                    launchMain()
                }

                EPGHandler.STATUS_FAIL -> {
                    logE("In Get EPG data callback fail ")
                    launchMain()
                }
            }
        }

    }

    private fun handleNetworkResponse(isConnected: Boolean) {
        if (!isConnected) {
            launchMain()
        } else {
            if (isCancellable) networkJob?.cancel()
        }
    }

    private fun launchMain() {
        networkJob?.cancel()
        networkJob = lifecycleScope.launch {
            delay(1000 * 15)
            MainMenuActivity::class.java.startActivity()
        }
    }

}