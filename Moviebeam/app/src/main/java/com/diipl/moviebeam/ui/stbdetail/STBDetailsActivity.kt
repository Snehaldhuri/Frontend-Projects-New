package com.diipl.moviebeam.ui.stbdetail

import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.ActivityStbdetailsBinding
import com.diipl.moviebeam.service.interceptors.RetryInterceptor
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.grantPermissions
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.setupSnackbar
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.startActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "STBDetailsActivity"
@AndroidEntryPoint
class STBDetailsActivity : BaseActivity() {

    private val viewModel: APIViewModel by viewModels()
    private lateinit var binding: ActivityStbdetailsBinding
    private var networkJob : Job? = null
    private var isCancellable = true

    override fun onBackPressed() {}

    override fun observeViewModel() {
        observe(viewModel.networkStatus, ::handleNetworkResponse)
        observe(viewModel.isLastApiCallFinished, ::handleApiCallFinishedResponse)

        observeSnackBarMessages(viewModel.showSnackBar)
        observeToast(viewModel.showToast)
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        binding.root.setupSnackbar(this, event, Snackbar.LENGTH_SHORT)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_SHORT)
    }

    override fun initViewBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stbdetails)
        grantPermissions()
        viewModel.getNetworkStatus()

        if (!preferenceHandler.isAllDataFetched)
            viewModel.fetchAllAPI()
        else {
            viewModel.fetchDataNotPresent()
        }
    }

    private fun handleNetworkResponse(isConnected: Boolean) {
        if (!isConnected) {
            launchMain(isConnected)
        } else {
            if (isCancellable)
                networkJob?.cancel()
        }
    }

    private fun handleApiCallFinishedResponse(isFinished: Boolean) {
        if (isFinished) {
            isCancellable = false
            launchMain()
        }
    }

    private fun launchMain(isConnected: Boolean = true) {
        networkJob?.cancel()
        networkJob = lifecycleScope.launch {
            while(isActive){
                if (RetryInterceptor.IS_RESPONSE_OK) {
                    val sec = if (!isConnected) 15L else 5L
                    delay(1000*sec)
                    preferenceHandler.updateDatastoreVariables(isAllDataFetched = RetryInterceptor.IS_RESPONSE_OK)
                    MainMenuActivity::class.java.startActivity()
                }
                delay(1000*30)
            }
        }
    }

}