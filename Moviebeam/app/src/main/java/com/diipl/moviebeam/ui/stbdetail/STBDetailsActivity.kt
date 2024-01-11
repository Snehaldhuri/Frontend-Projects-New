package com.diipl.moviebeam.ui.stbdetail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class STBDetailsActivity : BaseActivity() {

    private val stbDetailViewModel: STBDetailViewModel by viewModels()
    private lateinit var binding: ActivityStbdetailsBinding
    private var serialNumber: String = ""
    private var UA = ""

    private val preferenceDataStoreHelper = PreferenceDataStoreHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stbDetailViewModel.getDataFromDataStore(preferenceDataStoreHelper)
    }

    //observe class
    override fun observeViewModel() {
        observe(stbDetailViewModel.stbMasterLiveData, ::handleStbMasterResponse)
        observe(stbDetailViewModel.serialNoLiveData, ::handleSerialNumberResponse)

    }

    override fun initViewBinding() {
        binding = ActivityStbdetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }

    private fun handleStbMasterResponse(status: Resource<StbMasterResponse>) {
        when (status) {
            is Resource.Loading -> {}

            is Resource.Success -> {
                Log.d("responce", "handleStbMasterResponse: ")
                stbDetailViewModel.stbMasterLiveData.value?.data?.let {
                    Log.d("responce_stbMaster", "handleStbMasterResponse:${it}")
                    // binding.tvStb.text=it.
                }
                // binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it) }
            }
        }
    }

    private fun handleSerialNumberResponse(serialNo: String) {
        serialNumber = serialNo
        UA = "21$serialNumber"

        Log.d("UA", "handleSerialNumberResponse: $UA")

    }


    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }
}