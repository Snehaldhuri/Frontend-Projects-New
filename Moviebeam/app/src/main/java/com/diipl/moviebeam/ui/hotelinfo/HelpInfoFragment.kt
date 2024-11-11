package com.diipl.moviebeam.ui.hotelinfo

import android.content.Context
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.FragmentHelpInfoBinding
import com.diipl.moviebeam.service.remote.BTService
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.activityStack
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.dialogs.ParentalControlFragment
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.IRUtils
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.clearCache
import com.diipl.moviebeam.utils.getAppVersion
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.isPackageExists
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HelpInfoFragment(private var onBackButtonClick: () -> Unit) : BaseFragment() {

    //Variables from datastore
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var accountId: String = ""
    private var serialNo: String = ""
    private var ua: String = ""
    private var stbRoomNo: String = ""
    private var moviesCount = 0
    private var showsCount = 0
    private var cListVersion = ""
    private var ipAddress = "0.0.0.0"
    private var netMask = "0.0.0.0"
    private var gateway = "0.0.0.0"
    private var connectivity = "NO INTERNET"

    private var _binding: FragmentHelpInfoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferences: SharedPreference
    override fun observeViewModel() {

    }

    override fun initViewBinding() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logD("Switch to Help & Info")
        _binding = FragmentHelpInfoBinding.inflate(inflater, container, false)
        preferenceDataStoreHelper = PreferenceDataStoreHelper(requireContext())
        this.initializeDatastoreParams()
        when(BuildConfig.BUILD_TYPE){
            Constants.BUILD_TYPE_STB -> binding.layoutBrand.toGone()
        }
        try {
            activityStack.add(this::class.java.simpleName)
            setHotelInfo()
            val tvInputManager =
                requireActivity().getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
            val tvInputInfos = tvInputManager.tvInputList
            if (tvInputInfos.isNotEmpty()) {
                logD("Device is connected to an STB $tvInputInfos")
            } else {
                logD("Device is not connected to an STB")
            }

            val connectivityManager =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                logD("Device is connected to an WiFi")
            } else {
                logD("Device is not connected to an WiFi")
            }

            logD("In HelpInfoMain activity")
        } catch (e: Exception) {
            logD("Exception in Help & Info: ${e.message}")
            throw IllegalStateException("Failed to create view for HelpInfoFragment", e)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (BuildConfig.BUILD_TYPE == Constants.BUILD_TYPE_CHROMECAST) {
            binding.layoutBrand.toVisible()

            updateRadio()

            binding.rgType.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    binding.rbBT.id -> {
                        preferences.isIRRemote = false
                    }

                    binding.rbIR.id -> {
                        preferences.isIRRemote = true
                    }
                }
                updateRadio()
            }

            binding.rgBrand.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    binding.rbLg.id -> {
                        preferences.irFrequencyModel = IRUtils.lgModel
                        preferences.btCommandModel = BTService.lgModel
                    }

                    binding.rbSamsung.id -> {
                        preferences.irFrequencyModel = IRUtils.samsungModel
                        preferences.btCommandModel = BTService.samsungModel
                    }
                }
                requireActivity().clearCache()
            }
        }

    }

    private fun updateRadio() {
        if (preferences.isIRRemote) {
            binding.rbIR.isChecked = true
            if (preferences.irFrequencyModel.tvBrandName == IRUtils.SAMSUNG) {
                binding.rbSamsung.isChecked = true
            } else {
                binding.rbLg.isChecked = true
            }
        } else {
            binding.rbBT.isChecked = true
            if (preferences.btCommandModel.tvBrandName == IRUtils.SAMSUNG) {
                binding.rbSamsung.isChecked = true
            } else {
                binding.rbLg.isChecked = true
            }
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

        val adapterForHelpInfo = HelpInfoTabAdapter(list) { pos, v ->
            v.requestFocus()
            when (pos) {
                0 -> {
                    v.requestFocus()
                    binding.containerControl.toGone()
                    binding.cardInfo.toVisible()
                }

                1 -> {
                    binding.cardInfo.toGone()
                    binding.containerControl.toVisible()

                    val fragment = ParentalControlFragment { i ->
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

        context?.apply {
            if (isPackageExists(Constants.MDM_PACKAGE_NAME)){
                binding.tvMdmVersion.text = "MDM Version: ${getAppVersion(Constants.MDM_PACKAGE_NAME)}"
                binding.tvMdmVersion.toVisible()
            } else binding.tvMdmVersion.toGone()
            if (isPackageExists(Constants.PBTV_PACKAGE_NAME)){
                binding.tvPbtvVersion.text = "PBTV Version: ${getAppVersion(Constants.PBTV_PACKAGE_NAME)}"
                binding.tvPbtvVersion.toVisible()
            } else binding.tvPbtvVersion.toGone()
        }


        binding.tvAccountId.text = "Hotel Code: " + accountId
        binding.tvRoomNo.text = "Room No: " + stbRoomNo
        binding.tvUa.text = "UA: " + ua
        binding.tvSerialNo.text = "Serial No: " + serialNo
        binding.tvSoftwareVersion.text = "Software Version: " + BuildConfig.VERSION_NAME
        binding.tvContentListVersion.text = "Content List Version: $cListVersion"
        binding.tvContentCount.text =
            "Total Content Count: ${moviesCount.plus(showsCount)}"

        val ipAddress = "IP Address: $ipAddress"
        val netmask = "Net Mask: $netMask"
        val gateway = "Gateway: $gateway"
        val connectivity = "Connectivity: $connectivity"

        binding.tvIpAddress.text = ipAddress
        binding.tvNetMask.text = netmask
        binding.tvGateway.text = gateway
        binding.tvConnectivity.text = connectivity
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            accountId = preferenceHandler.accountID
            serialNo = preferenceHandler.serialNo
            ua = preferenceHandler.UA
            stbRoomNo = preferenceHandler.roomNo
            moviesCount = preferenceHandler.movieCount
            showsCount = preferenceHandler.showsCount
            cListVersion = preferenceHandler.cListVersion
            ipAddress = preferenceHandler.ipAddress
            netMask = preferenceHandler.netMask
            gateway = preferenceHandler.gatewayIP
            connectivity = preferenceHandler.connectivity
        }
    }

}

