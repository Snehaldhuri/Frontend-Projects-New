package com.diipl.moviebeam.ui.hotelinfo

import android.content.Context
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.databinding.FragmentHelpInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.dialogs.ParentalControlFragment
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HelpInfoFragment"

@AndroidEntryPoint
class HelpInfoFragment(private var onBackButtonClick: () -> Unit) : Fragment() {

    private var _binding: FragmentHelpInfoBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var preferences : SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpInfoBinding.inflate(inflater, container, false)

        try {
            activityStack.add(this::class.java.simpleName)

            setHotelInfo()

            val ipAddress = "IP Address: " + Constants.IP_ADDRESS
            val netmask = "Net Mask: " + Constants.IP_NET_MASK
            val gateway = "Gateway: " + Constants.IP_GATEWAY
            val connectivity = "Connectivity: " + Constants.CONNECTIVITY

            binding.tvIpAddress.text = ipAddress
            binding.tvNetMask.text = netmask
            binding.tvGateway.text = gateway
            binding.tvConnectivity.text = connectivity


            val tvInputManager =
                requireActivity().getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
            val tvInputInfos = tvInputManager.tvInputList
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

            LoggingService.sendMessageToWebSocket("In HelpInfoMain activity", "07")
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("${e.message}", "07")
            throw IllegalStateException("Failed to create view for HelpInfoFragment", e)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (preferences.irFrequencyModel == null)
            preferences.irFrequencyModel = IRUtils.SELECTED_BRAND

        if (preferences.irFrequencyModel.tvBrandName == IRUtils.SAMSUNG){
            binding.rbSamsung.isChecked = true
        } else {
            binding.rbLg.isChecked = true
        }

        binding.rgBrand.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                binding.rbLg.id -> {
                    preferences.irFrequencyModel = IRUtils.lgModel
                }
                binding.rbSamsung.id -> {
                    preferences.irFrequencyModel = IRUtils.samsungModel
                }
            }
            requireActivity().clearCache()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.handleFocusChange()
        binding.rvHelpInfoHeader.handleFocusChange()

        binding.rvHelpInfoHeader.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.btnBack.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    binding.rvHelpInfoHeader.post {
                        binding.rvHelpInfoHeader.requestFocus()
                    }
                }
            }

            false
        }
        binding.btnBack.setOnClickListener {
            onBackButtonClick()
        }

        val list = mutableListOf<String>()
        list.add(Constants.SYSTEM_INFO)
        list.add(Constants.TAB_PARENTAL_CONTROL)

        val adapterForHelpInfo = HelpInfoTabAdapter(list){pos, v->
            v.requestFocus()
            when(pos) {
                0 -> {
                    v.requestFocus()
                    binding.containerControl.toGone()
                    binding.cardInfo.toVisible()
                }
                1 -> {
                    binding.cardInfo.toGone()
                    binding.containerControl.toVisible()

                    val fragment = ParentalControlFragment{ i ->
                        v.requestFocus()
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(binding.containerControl.id, fragment).commitNow()

                }
            }
        }

        binding.rvHelpInfoHeader.adapter = adapterForHelpInfo
        binding.rvHelpInfoHeader.post {
            binding.rvHelpInfoHeader.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
        }

    }


    private fun setHotelInfo() {
        binding.tvAccountId.text = "Hotel Code: " + Constants.ACCOUNT_ID
        binding.tvRoomNo.text = "Room No: " + Constants.STB_ROOM_NO
        binding.tvUa.text = "UA: " + Constants.UA
        binding.tvSerialNo.text = "Serial No: " + Constants.SERIAL_NO
        binding.tvSoftwareVersion.text = "Software Version: " + BuildConfig.VERSION_NAME
        binding.tvContentListVersion.text = "Content List Version: ${Constants.C_LIST_VERSION}"
        binding.tvContentCount.text =
            "Total Content Count: ${Constants.MOVIES_COUNT.plus(Constants.SHOWS_COUNT)}"
    }

}

