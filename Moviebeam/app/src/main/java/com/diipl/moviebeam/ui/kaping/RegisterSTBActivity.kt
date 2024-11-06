package com.diipl.moviebeam.ui.kaping

import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityKapingBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.service.kappingservice.EndlessService
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.CONFIG_DATA_KEY
import com.diipl.moviebeam.utils.KapingConstants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.getConnectivityType
import com.diipl.moviebeam.utils.launchNewActivity
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.openSettingsPattern
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterSTBActivity : BaseActivity() {

    private val TAG = "RegisterSTBActivity"

    private val registerSTBViewModel: RegisterSTBViewModel by viewModels()
    private lateinit var binding: ActivityKapingBinding

    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var ipAddress = "0.0.0.0"
    private var netmask = "0.0.0.0"
    private var gateway = "0.0.0.0"

    @Inject
    lateinit var hardwareAPI: HardwareAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        binding.root.openSettingsPattern()

        initializeDatastoreParams()

        binding.tvIp.text = ipAddress
        binding.tvNetMask.text = netmask
        binding.tvGateway.text = gateway

        binding.tvSwVersion.text = BuildConfig.VERSION_NAME
        binding.tvConnectivity.text = getConnectivityType(applicationContext)

        validateAsFlag()
    }

    private fun validateAsFlag() {
        lifecycleScope.launch {
            //checking as flag
            if (EndlessService.AS_FLAG) {
                logD("AS Flag is True")
                registerSTBViewModel.fetchAccountAPI(preferenceHandler.UA)
            } else {
                delay(60 * 1000)
                validateAsFlag()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun observeViewModel() {
        observe(registerSTBViewModel.accountSetupLiveData, ::handleAccountResponse)
        observe(registerSTBViewModel.stbMasterLiveData, ::handleStbMasterResponse)
        observeSnackBarMessages(registerSTBViewModel.showSnackBar)
        observeToast(registerSTBViewModel.showToast)
    }

    private fun handleAccountResponse(resource: Resource<AccountSetupResponse>) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { sendDataMDM(it) }
            }

            is Resource.DataError -> {
                handleRebootCmd()
            }

            else -> {}
        }
    }

    private fun sendDataMDM(response: AccountSetupResponse) = lifecycleScope.launch {
        val configData =
            "${preferenceHandler.serialNo}, ${response.accountId}, ${response.roomNo}, ${response.mdmServerUrl}, ${response.mdmServerUsername}, ${response.mdmServerPassword}"
        Log.e(TAG, "sendDataMDM: $configData")

        Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName(Constants.MDM_PACKAGE_NAME, Constants.MDM_UPDATE_DATA)
            putExtra(CONFIG_DATA_KEY, configData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }

        delay(1000 * 10L)

        launchNewActivity(STBDetailsActivity::class.java, true)
    }

    private fun handleRebootCmd() {
        showToast("Account API call failed.")

        preferenceHandler.updateDatastoreVariables(isStbAllocated = false)

        when (BuildConfig.BUILD_TYPE) {
            Constants.BUILD_TYPE_STB -> {
                hardwareAPI.myService?.rebootDevice()
            }

            else -> {
                val intent = Intent()
                intent.component =
                    ComponentName(
                        Constants.MDM_PACKAGE_NAME,
                        KapingConstants.MDM_RESTART_ACTIVITY_NAME
                    )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun handleSerialNumberResponse(serialNo: String) {
        val ua = "${Constants.UA_PREFIX}$serialNo"
        binding.tvUa.text = ua
        binding.tvSerialNo.text = serialNo
        binding.ivQrCode.setImageBitmap(generateQRCode(serialNo))
        registerSTBViewModel.processSTBMaster(
            ua,
            serialNo,
            Constants.MAC_ADDRESS,
            Constants.WIFI_MAC_ADDRESS,
            Constants.STB_TYPE
        )
    }

    private fun handleStbMasterResponse(status: Resource<StbMasterResponse>) {
        when (status) {
            is Resource.Success -> {
                val response = registerSTBViewModel.stbMasterLiveData.value?.data
                when (response?.errorCode) {
                    400, 402 -> {
                        binding.tvStb.text = "Yes"
                        registerSTBViewModel.updateStbStatus(preferenceDataStoreHelper, true)
                    }

                    else -> {
                        registerSTBViewModel.updateStbStatus(preferenceDataStoreHelper, false)
                    }
                }
            }

            else -> {
                status.errorCode?.let { registerSTBViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { registerSTBViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    override fun initViewBinding() {
        binding = ActivityKapingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun generateQRCode(str: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 400, 400)

        val w = bitMatrix.width
        val h = bitMatrix.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    }

    override fun onBackPressed() {}

    private fun initializeDatastoreParams() {
        ipAddress = preferenceHandler.ipAddress
        netmask = preferenceHandler.netMask
        gateway = preferenceHandler.gatewayIP
        handleSerialNumberResponse(preferenceHandler.serialNo)
    }

}
