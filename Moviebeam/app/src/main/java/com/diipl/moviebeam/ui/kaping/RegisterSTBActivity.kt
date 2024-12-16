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
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.isNotEmptyOrNull
import com.diipl.moviebeam.utils.launchNewActivity
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.openSettingsPattern
import com.diipl.moviebeam.utils.rebootDevice
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterSTBActivity : BaseActivity() {

    private val TAG = "RegisterSTBActivity"

    private val registerSTBViewModel: RegisterSTBViewModel by viewModels()
    private lateinit var binding: ActivityKapingBinding

    private val preferenceDataStoreHelper by lazy { PreferenceDataStoreHelper(this) }

    @Inject
    lateinit var hardwareAPI: HardwareAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reboot = intent.getStringExtra("btnId")

        binding.tvSwVersion.text = BuildConfig.VERSION_NAME
        binding.root.openSettingsPattern()

        lifecycleScope.launch {
            while (isActive) {
                loadIP()
                if (preferenceHandler.ipAddress != preferenceHandler.CONSTANT_IP)
                    this.cancel()
                delay(1000 * 2)
            }
        }

        if (reboot == Constants.REBOOT_BTN)
            handleRebootCmd(false)
        else {
            handleSerialNumberResponse(preferenceHandler.serialNo)
            validateAsFlag()
        }

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
//        observe(registerSTBViewModel.serialNoLiveData, ::handleSerialNumberResponse)
        observe(registerSTBViewModel.accountSetupLiveData, ::handleAccountResponse)
        observe(registerSTBViewModel.stbMasterLiveData, ::handleStbMasterResponse)
        observeSnackBarMessages(registerSTBViewModel.showSnackBar)
        observeToast(registerSTBViewModel.showToast)
    }

    private fun handleAccountResponse(resource: Resource<AccountSetupResponse>) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.mdmServerUrl.isNotEmptyOrNull())
                        sendDataMDM(it)
                    else handleRebootCmd()
                }
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

    private fun handleRebootCmd(isLog: Boolean = true) {
        if (isLog) {
            val msg = "Account API call failed and MDM server is not found."
            logE("${msg.replace(".", "")} after 3 times retry.")
            showToast(msg)
        } else {
            logE("UA: ${preferenceHandler.UA} is removed from HID: ${preferenceHandler.accountID}. Rebooting device...")
        }

        preferenceHandler.updateDatastoreVariables(isStbAllocated = false)

        rebootDevice()
    }

    private fun handleSerialNumberResponse(serialNo: String) {
        Log.e(TAG, "handleSerialNumberResponse: $serialNo")
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
                        lifecycleScope.launch {
                            preferenceHandler.updateDatastoreVariables(isStbRegistered = false)
                            delay(5*1000)
                            handleSerialNumberResponse(preferenceHandler.serialNo)
                            delay(100)
                            this.cancel()
                        }
                    }
                }
            }

            else -> {
                status.errorCode?.let { registerSTBViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { registerSTBViewModel.showToastMessage(it) }
                lifecycleScope.launch {
                    preferenceHandler.updateDatastoreVariables(isStbRegistered = false)
                    delay(5*1000)
                    handleSerialNumberResponse(preferenceHandler.serialNo)
                    delay(100)
                    this.cancel()
                }
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

    private fun loadIP() {
        binding.root.postDelayed({
            binding.tvIp.text = preferenceHandler.ipAddress
            binding.tvNetMask.text = preferenceHandler.netMask
            binding.tvGateway.text = preferenceHandler.gatewayIP
            binding.tvConnectivity.text = preferenceHandler.connectivity
        }, 200)
    }

}
