package com.diipl.moviebeam.ui.hotelinfo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentHelpInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.utils.intToString

private const val TAG = "HelpInfoFragment"

class HelpInfoFragment(private var onBackButtonClick: () -> Unit) : Fragment() {

    private var _binding: FragmentHelpInfoBinding? = null
    private val binding get() = _binding!!

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpInfoBinding.inflate(inflater, container, false)
        activityStack.add(this::class.java.simpleName)
        arguments?.let {

            gradientStartColor = it.getString("gradientStartColor").toString()
            gradientEndColor = it.getString("gradientEndColor").toString()

        }
        setHotelInfo()
        binding.btnBack.post {
            binding.btnBack.requestFocus()
        }

        val wifiManager =
            requireActivity().applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        val ipAddress = "IP Address: " + dhcpInfo.ipAddress.intToString()
        val netmask = "Net Mask: " + dhcpInfo.netmask.intToString()
        val gateway = "Gateway: " + dhcpInfo.gateway.intToString()

        binding.tvIpAddress.text = ipAddress
        binding.tvNetMask.text = netmask
        binding.tvGateway.text = gateway


        val tvInputManager =
            requireActivity().getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
        val tvInputInfos = tvInputManager.tvInputList[1].loadLabel(requireActivity())
        if (tvInputInfos.isNotEmpty()) {
            Log.e(TAG, "Device is connected to an STB $tvInputInfos")
        } else {
            Log.e(TAG, "Device is not connected to an STB")
        }

        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.e(TAG, "Device is connected to an WiFi")
        } else {
            Log.e(TAG, "Device is connected to an WiFi")
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                view.background = getGradient(gradientStartColor, gradientEndColor)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                binding.rvHelpInfoHeader.post {
                                    binding.rvHelpInfoHeader.requestFocus()
                                }
                                binding.rvHelpInfoHeader.setOnFocusChangeListener { b, focus ->
                                    if (focus) {
                                        b.background =
                                            getGradient(gradientStartColor, gradientEndColor)
                                        b.setOnKeyListener { _, keyCode, event ->
                                            if (event.action == KeyEvent.ACTION_DOWN) {
                                                when (keyCode) {
                                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                                        binding.btnBack.post {
                                                            binding.btnBack.requestFocus()
                                                        }
                                                        return@setOnKeyListener true
                                                    }

                                                }
                                            }
                                            false
                                        }
                                    } else {
                                        b.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                                    }
                                }
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }

            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        binding.btnBack.setOnClickListener {
            handleBackClick()
        }

        val adapterForHelpInfo = HelpInfoTabAdapter(mutableListOf(Constants.SYSTEM_INFO))
        if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
            adapterForHelpInfo.setGradientColor(gradientStartColor, gradientEndColor)
        }
        binding.rvHelpInfoHeader.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvHelpInfoHeader.adapter = adapterForHelpInfo

    }


    private fun getGradient(startColor: String, endColor: String): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun handleBackClick() {
        onBackButtonClick()
    }

    private fun setHotelInfo() {
        binding.tvAccountId.text = "Hotel Code: " + Constants.ACCOUNT_ID
        binding.tvRoomNo.text = "Room No: " + Constants.STB_ROOM_NO
        binding.tvUa.text = "UA: " + Constants.UA
        binding.tvSerialNo.text = "Serial No: " + Constants.SERIAL_NO
        binding.tvSoftwareVersion.text = "Software Version: " + BuildConfig.VERSION_NAME
        binding.tvContentListVersion.text = "Content List Version: " + Constants.C_LIST_VERSION
    }

}

