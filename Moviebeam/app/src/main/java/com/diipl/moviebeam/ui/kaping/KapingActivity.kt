package com.diipl.moviebeam.ui.kaping

import com.diipl.moviebeam.ui.base.BaseActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData

import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.kaping.kapingResponce
import com.diipl.moviebeam.databinding.ActivityKapingBinding
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KapingActivity : BaseActivity() {

    private val kapingViewMmodel: KapingViewModel by viewModels()
    private lateinit var binding: ActivityKapingBinding

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
