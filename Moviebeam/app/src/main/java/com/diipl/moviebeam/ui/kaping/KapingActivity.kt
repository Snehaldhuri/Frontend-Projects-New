package com.diipl.moviebeam.ui.kaping

import android.content.Context
import android.content.Intent
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.diipl.moviebeam.ui.base.BaseActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.Constants

import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.kaping.kapingResponce
import com.diipl.moviebeam.databinding.ActivityKapingBinding
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.intToString
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KapingActivity : BaseActivity() {

    private val kapingViewMmodel: KapingViewModel by viewModels()
    private lateinit var binding: ActivityKapingBinding







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //checkig as flag
        if (Constants.AS_FLAG == true) {
            Log.e("as_flag_true", "onCreate:${Constants.AS_FLAG}")
            val intent = Intent(this@KapingActivity, MainMenuActivity::class.java)
            startActivity(intent)
        } else {
            Log.e("as_flag_false", "onCreate:${Constants.AS_FLAG}")
            val wifiManager = applicationContext.getSystemService(
                AppCompatActivity.WIFI_SERVICE
            ) as WifiManager
            val dhcpInfo = wifiManager.dhcpInfo
            val ipAddress = "IP Address: " + dhcpInfo.ipAddress.intToString()
            val netmask = "Net Mask: " + dhcpInfo.netmask.intToString()
            val gateway = "Gateway: " + dhcpInfo.gateway.intToString()

            Log.e(
                "ip_mask_gate",
                "handleKapingResponce:${ipAddress}${netmask}${gateway}",
            )
            binding.tvIp.text = ipAddress
            binding.tvNetMask.text = netmask
            binding.tvGateway.text = gateway


            val tvInputManager =
                getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
            /*  val tvInputInfos =
                  tvInputManager.tvInputList[1].loadLabel(applicationContext)
              if (tvInputInfos.isNotEmpty()) {
                  Log.e(
                      "tvInputInfos_connected",
                      "Device is connected to an STB $tvInputInfos"
                  )
              } else {
                  Log.e("tvInputInfos_not_connected", "Device is not connected to an STB")
              }*/

            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.activeNetwork
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.e("TAG", "Device is connected to an WiFi")
            } else {
                Log.e("TAG", "Device is connected to an WiFi")
            }
        }
    }






    override fun observeViewModel() {
        observe(kapingViewMmodel.kapingLiveData, ::handleKapingResponce)

        observeSnackBarMessages(kapingViewMmodel.showSnackBar)
        observeToast(kapingViewMmodel.showToast)
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


    fun handleKapingResponce(status: Resource<kapingResponce>) {
        when (status) {
            is Resource.Loading -> {}

            is Resource.Success -> {
                Log.d("responce", "handleStbMasterResponse: ")
                kapingViewMmodel.kapingLiveData.value?.data?.let {
                    Log.d("responce_stbMaster", "handleStbMasterResponse:${it}")
                    // binding.tvStb.text=it.
                }
                // binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { kapingViewMmodel.showToastMessage(getString(it)) }
                status.errorMsg?.let { kapingViewMmodel.showToastMessage(it) }
            }
        }
    }
}
