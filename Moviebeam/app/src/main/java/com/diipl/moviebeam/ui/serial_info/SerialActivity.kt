package com.diipl.moviebeam.ui.serial_info

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.tv.settings.aidl.common.ISeiCommonApi
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivitySerialBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.kappingservice.Actions
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.launchNewActivity
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class SerialActivity : BaseActivity() {


    private lateinit var binding: ActivitySerialBinding
    private val serialViewModel: SerialViewModel by viewModels()
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(
            applicationContext
        )
    }

    @Inject
    lateinit var hardwareAPI: HardwareAPI


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
        serialViewModel.getDataFromDataStore(preferenceDataStoreHelper)
    }


    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun fetchSerialNo() {
        when(BuildConfig.BUILD_TYPE){
            Constants.BUILD_TYPE_STB -> {
                fetchSerialFromSDK()
            }
            else -> {
                val intent = Intent()
                intent.component = ComponentName(Constants.MDM_PACKAGE_NAME, Constants.MDM_SERIAL_ACTIVITY)
                resultLauncher.launch(intent)
            }
        }
    }

    private fun fetchSerialFromSDK() {
        hardwareAPI.myService?.let {
            Log.e(TAG, "fetchSerialFromSDK: ${it.deviceSn}")
            processSerialNo(it.deviceSn)
        }
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
        logD("processSerialNo: $serialNo")
        val ua = "${Constants.UA_PREFIX}${serialNo}"
        serialViewModel.setDataInDataStore(
            preferenceDataStoreHelper,
            true,
            serialNo,
            ua
        )
        redirectToRegisterStbActivity()
    }

    private fun handleDataStoreResponse(isSerialNoTaken: Boolean) {
        if (isSerialNoTaken) {
            logD("Found Serial No in Datastore")
            serialViewModel.getStbStatusFromDataStore(preferenceDataStoreHelper)
        } else {
            logD("Requesting for Serial No from MDM")
            fetchSerialNo()
        }
        actionOnService(Actions.START)
    }

    private fun handleStbStatusResponse(isStbRegistered: Boolean) {
        if (isStbRegistered) {
            serialViewModel.getStbAllocationStatusFromDataStore(preferenceDataStoreHelper)
            logD("In App Loader create")
        } else {
            redirectToRegisterStbActivity()
            logD("Showing Landing Page")
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
        launchNewActivity(STBDetailsActivity::class.java, true)
    }

    private fun redirectToRegisterStbActivity() {
        launchNewActivity(RegisterSTBActivity::class.java, true)
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
                    logD("Starting the service in >=26 Mode")
                    startForegroundService(it)
                    return
                } else {
                    logD("Starting the service in < 26 Mode")
                    startService(it)
                }
            }
        }
    }

    override fun onBackPressed() {}

}
