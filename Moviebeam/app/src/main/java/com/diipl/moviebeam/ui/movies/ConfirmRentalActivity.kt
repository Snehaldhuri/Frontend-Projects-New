package com.diipl.moviebeam.ui.movies

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.movies.AdultDayPassRequest
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.DayPassResponse
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_FINISH_TIME
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants.ADULT_DAY_PASS_STATUS
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityConfirmRentalBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.fromJson
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toJson
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmRentalActivity : BaseActivity() {

    private lateinit var binding: ActivityConfirmRentalBinding
    private lateinit var movie: ContentDto
    private val viewModel: MoviesViewModel by viewModels()
    private var isCheckedIn = false
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var ua = ""
    private lateinit var passPrice: String

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    override fun observeViewModel() {
        observe(viewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(viewModel.rentalMovieResponse, ::handleMovieResponse)
//        observe(viewModel.themeLiveData, ::handleThemeResponse)
        observe(viewModel.purchaseResponse, ::handlePurchaseResponse)
        observeToast(viewModel.showToast)

//        viewModel.getThemeResponseData(themeDataStore)
    }

    private fun handlePurchaseResponse(resource: Resource<DayPassResponse>) {
        when (resource) {
            is Resource.Loading -> {
                binding.layoutPass.toGone()
                binding.progressBar.toVisible()
            }

            is Resource.Success -> {
                resource.data?.let {
                    when (it.errorCode) {
                        0 -> {
                            lifecycleScope.launch {
                                preferenceDataStoreHelper.putPreference(ADULT_DAY_PASS_STATUS, true)
                                preferenceDataStoreHelper.putPreference(
                                    ADULT_DAY_PASS_FINISH_TIME,
                                    System.currentTimeMillis().plus(24 * 60 * 60 * 1000)
                                )
                                activityStack.add(Constants.C_TYPE_MOVIE)
                                finish()
                            }
                        }

                        2 -> {
                            viewModel.showToastMessage(getString(R.string.insufficient_balance))
                        }

                        else -> {
                            viewModel.showToastMessage(getString(R.string.call_front_desk))
                        }
                    }
                }
            }

            else -> {
                binding.progressBar.toGone()
                binding.layoutPass.toVisible()
                viewModel.showToastMessage(getString(R.string.call_front_desk))
            }
        }
    }

    override fun initViewBinding() {
        binding = ActivityConfirmRentalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        this.initializeDatastoreParams()
        binding.btnConfirm.handleFocusChange()
        binding.btnCancel.handleFocusChange()
        binding.btnBuyNow.handleFocusChange()
        binding.btnPassCancel.handleFocusChange()

        viewModel.validateSession(preferenceDataStoreHelper)

        intent.getStringExtra(Constants.MOVIE_RENTALS)?.let {
            movie = it.fromJson()
            binding.tvMovieName.text = movie.movieName
            binding.tvPriceConfirm.text =
                getString(R.string.rental_price_confirm, "", movie.price.toString())
        }
        if (::movie.isInitialized) {
            binding.layoutRental.toVisible()
            binding.btnConfirm.requestFocus()
        } else {
            binding.layoutPass.toVisible()
            binding.btnBuyNow.requestFocus()
        }
        intent.getStringExtra("price")?.let {
            binding.tvAdultPrice.text = getString(R.string.adult_pass_price, "", it)
            passPrice = it
        }


        binding.btnConfirm.setOnClickListener {
            val request = RentalMovieRequest()
            request.UA = ua
            request.productId = movie.productId
            request.releaseID = movie.releaseId
            request.price = movie.price
            request.contentTypeID = movie.contentTypeId
            request.productType = movie.releaseTypeId
            if (isCheckedIn) {
                viewModel.getRentalMovieResponse(request)
            } else {
                viewModel.showToastMessage(getString(R.string.call_front_desk))
            }
        }
        binding.btnBuyNow.setOnClickListener {
            val request = AdultDayPassRequest()
            lifecycleScope.launch {
                request.UA = preferenceDataStoreHelper.getFirstPreference(
                    PreferenceDataStoreConstants.UA, ""
                )
            }
            request.price = (passPrice.toInt() * 100)
            if (isCheckedIn) {
                viewModel.buyPassRequest(request)
            } else {
                viewModel.showToastMessage(getString(R.string.call_front_desk))
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnPassCancel.setOnClickListener {
            finish()
        }
    }

    private fun handleMovieResponse(state: Resource<RentalMovieResponse>) {
        when (state) {
            is Resource.Loading -> {
                binding.layoutRental.toGone()
                binding.progressBar.toVisible()
            }

            is Resource.Success -> {
                state.data?.let { data ->
                    when (data.errorCode) {
                        0 -> {
                            startActivity(data)
                        }

                        1 -> {
                            binding.progressBar.toGone()
                            binding.layoutRental.toVisible()
                            viewModel.showToastMessage(getString(R.string.product_is_currently_unavailable))
                        }

                        2 -> {
                            binding.progressBar.toGone()
                            binding.layoutRental.toVisible()
                            viewModel.showToastMessage(getString(R.string.please_contact_the_front_desk_for_assistance))
                        }

                        3 -> {
                            binding.progressBar.toGone()
                            binding.layoutRental.toVisible()
                            viewModel.showToastMessage(getString(R.string.call_front_desk_to_activate_moviebeam_services))
                        }

                        else -> {
                            binding.progressBar.toGone()
                            binding.layoutRental.toVisible()
                            binding.btnConfirm.requestFocus()
                        }

                    }
                }

            }

            else -> {
                binding.progressBar.toGone()
                binding.layoutRental.toVisible()
                viewModel.showToastMessage(state.errorMsg.toString())
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {


            }

            else -> {
                status.errorCode?.let { viewModel.showToastMessage(getString(it)) }
            }
        }
    }


    private fun handleValidateSessionResponse(status: Boolean) {
        this.isCheckedIn = status
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun startActivity(data: RentalMovieResponse) {

        viewModel.insertMovieDetails(data, movie)

        val bundle = Bundle()
        bundle.putString(Constants.MOVIE_DETAILS, movie.toJson())
        bundle.putBoolean(Constants.IS_TRAILER, false)
        bundle.putBoolean(Constants.IS_CONTENT, true)
        bundle.putLong(Constants.IS_CONTINUE, 0)

        val intent = Intent(this, ExoPlayerActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            ua = getUa()
        }
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

}