package com.diipl.moviebeam.ui.casting

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import com.android.tv.settings.aidl.regular.ISoftAPConfigureCallback
import com.diipl.moviebeam.databinding.ActivityHotspotBinding
import com.diipl.moviebeam.di.HardwareAPI
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.loadBg
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "HotspotActivity"

@AndroidEntryPoint
class HotspotActivity : BaseActivity() {

    private lateinit var binding: ActivityHotspotBinding

    @Inject
    lateinit var hardwareAPI: HardwareAPI
    private var hotSpotConfig: HotSpotDetails? = null

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityHotspotBinding.inflate(layoutInflater)
        binding.root.loadBg()

        setContentView(binding.root)
        checkWifiHotspotStatus()

    }

    private fun checkWifiHotspotStatus() {
        hardwareAPI.myService?.let {
            try {
                Log.e(TAG, "fetchSerialFromSDK: ${it.deviceSn} ${it.softApStatus}")
                hotSpotConfig = parseHotSpotConfig(it.softApStatus.trimIndent())
                if (!hotSpotConfig?.hotSpotSwitch!!) {
                    //HotSpot is off
                    //turn on hotspot programmatically
                    turnOnHotSpotProgrammatically()
                } else {
                    //HotSpot is ON
                    setHotSpotDetails(hotSpotConfig)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val iSoftAPConfigureCallback = object : ISoftAPConfigureCallback.Stub() {
        @Throws(RemoteException::class)
        override fun onSoftAPConfigureCallback(s: String) {
            if (s.equals("Wifi AP opened")) {
                setHotSpotDetails(hotSpotConfig)
            }
        }
    }

    private fun turnOnHotSpotProgrammatically() {
        hardwareAPI.myService?.openSoftAP(true, iSoftAPConfigureCallback)
    }

    private fun setHotSpotDetails(hotSpotConfig: HotSpotDetails?) {
        if(hotSpotConfig!=null) {
            binding.tvNetworkErrorHotspot.visibility=View.GONE
            binding.wifiName.text = hotSpotConfig.ssid ?: "NA"
            binding.wifiPassword.text = hotSpotConfig.password ?: "NA"
            binding.qrCodeImage.setImageBitmap(
                createQRCodeForHotSpot(
                    hotSpotConfig.ssid ?: "NA",
                    hotSpotConfig.password ?: "NA",
                )
            )
        }else{
            binding.containerHotspot.visibility= View.GONE
            binding.tvNetworkErrorHotspot.visibility=View.VISIBLE
        }
    }


    private fun createQRCodeForHotSpot(ssid: String, password: String): Bitmap {
        val qrCodeContent = "WIFI:S:$ssid;T:WPA;P:$password;;"
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(qrCodeContent, BarcodeFormat.QR_CODE, 500, 500)

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
        Log.e(TAG, "createQRCodeForHotSpot: bitmap: ${bitmap.width} ${bitmap.height}", )
        return bitmap
    }

    private fun parseHotSpotConfig(configString: String): HotSpotDetails {
        val configLines = configString.split("\n")
        val configMap = mutableMapOf<String, String>()

        for (line in configLines) {
            val keyValue = line.split(": ", limit = 2)
            if (keyValue.size == 2) {
                configMap[keyValue[0]] = keyValue[1]
            }
        }

        return HotSpotDetails(
            hotSpotSwitch = configMap["HotSpot switch"]?.toBoolean() ?: false,
            ssid = configMap["HotSpot SSID"].orEmpty(),
            password = configMap["HotSpot Password"].orEmpty(),
            band = configMap["HotSpot Band"].orEmpty()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    data class HotSpotDetails(
        val hotSpotSwitch: Boolean,
        val ssid: String,
        val password: String,
        val band: String
    )
}