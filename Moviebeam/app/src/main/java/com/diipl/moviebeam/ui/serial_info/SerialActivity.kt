package com.diipl.moviebeam.ui.serial_info

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivitySerialBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.kappingservice.Actions
import com.diipl.moviebeam.ui.kappingservice.EndlessService
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.launchLogger
import com.diipl.moviebeam.utils.log
import com.diipl.moviebeam.utils.observe


private const val TAG = "SerialActivity"

class SerialActivity : BaseActivity() {

    private lateinit var binding: ActivitySerialBinding
    private val serialViewModel: SerialViewModel by viewModels()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    override fun observeViewModel() {
        observe(serialViewModel.serialNoTakenLiveData, ::handleDataStoreResponse)
        observe(serialViewModel.stbStatusLiveData, ::handleStbStatusResponse)
        observe(serialViewModel.stbAllocationStatusLiveData, ::handleStbAllocationStatusResponse)
    }

    override fun initViewBinding() {
        binding = ActivitySerialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        serialViewModel.getDataFromDataStore(preferenceDataStoreHelper)
        launchLogger()
    }

    private fun fetchSerialNo() {
        val intent = Intent()
        intent.component = ComponentName(Constants.MDM_PACKAGE_NAME, Constants.MDM_SERIAL_ACTIVITY)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                intent?.getStringExtra(Constants.SERIAL_NO_KEY)?.let {
                    processSerialNo(it)
                }
            }
        }

    private fun processSerialNo(serialNo: String) {
        Log.e(TAG, "processSerialNo: $serialNo")
        Constants.SERIAL_NO = serialNo
        Constants.UA = "21${Constants.SERIAL_NO}"
        serialViewModel.setDataInDataStore(
            preferenceDataStoreHelper,
            true,
            Constants.SERIAL_NO,
            Constants.UA
        )
        redirectToRegisterStbActivity()
    }

    private fun handleDataStoreResponse(isSerialNoTaken: Boolean) {
        if (isSerialNoTaken) {
            serialViewModel.getStbStatusFromDataStore(preferenceDataStoreHelper)
        } else {
//            fetchSerialNo()
            val serialNo = "29221HFGN30WLA"
            processSerialNo(serialNo)
        }
        actionOnService(Actions.START)
    }

    private fun handleStbStatusResponse(isStbRegistered: Boolean) {
        if (isStbRegistered) {
            serialViewModel.getStbAllocationStatusFromDataStore(preferenceDataStoreHelper)
            LoggingService.sendMessageToWebSocket("In App Loader create ", getCurrentPanelNumber())
        } else {
            redirectToRegisterStbActivity()
            LoggingService.sendMessageToWebSocket("Showing Landing Page", getCurrentPanelNumber())
        }
    }

    private fun handleStbAllocationStatusResponse(isStbAllocated: Boolean) {
        if (isStbAllocated) {
            redirectToStbDetailsActivity()
        } else {
            redirectToRegisterStbActivity()
        }
    }

    private fun redirectToStbDetailsActivity() {
        startActivity(Intent(this, STBDetailsActivity::class.java))
        finish()
    }

    private fun redirectToRegisterStbActivity() {
        startActivity(Intent(this, RegisterSTBActivity::class.java))
        finish()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun actionOnService(action: Actions) {
        if (!EndlessService.isServiceStarted) {
            Intent(this, EndlessService::class.java).also {
                it.action = action.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    log("Starting the service in >=26 Mode")
                    startForegroundService(it)
                    return
                } else {
                    log("Starting the service in < 26 Mode")
                    startService(it)
                }
            }
        }
    }

    override fun onBackPressed() {}

}
