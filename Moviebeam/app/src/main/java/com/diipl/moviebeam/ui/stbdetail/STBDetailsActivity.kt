package com.diipl.moviebeam.ui.stbdetail

import android.os.Build.VERSION_CODES.S
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.stbdetail.StbMasterResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityMainMenuBinding
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuViewModel
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class STBDetailsActivity : BaseActivity() {

    private val stbDetailViewModel: STBDetailViewModel by viewModels()
    private lateinit var binding: ActivityStbdetailsBinding


    //observe class
    override fun observeViewModel() {
        observe(stbDetailViewModel.stbMasterLiveData, ::handleStbMasterResponse)

        observeSnackBarMessages(stbDetailViewModel.showSnackBar)
        observeToast(stbDetailViewModel.showToast)
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
                Log.d("responce" ,"handleStbMasterResponse: ")
                stbDetailViewModel.stbMasterLiveData.value?.data?.let {
                    Log.d("responce_stbMaster", "handleStbMasterResponse:${it}")
                   // binding.tvStb.text=it.
                }
                // binding.pbLoader.toInvisible()
            }
            else -> {
                status.errorCode?.let { stbDetailViewModel.showToastMessage(getString(it)) }
                status.errorMsg?.let { stbDetailViewModel.showToastMessage(it)}
            }
        }
    }


    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }
}