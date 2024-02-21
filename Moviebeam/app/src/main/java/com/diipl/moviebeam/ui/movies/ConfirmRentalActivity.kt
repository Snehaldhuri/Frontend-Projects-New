package com.diipl.moviebeam.ui.movies

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.LiveData
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.RentalMovieRequest
import com.diipl.moviebeam.data.dto.movies.RentalMovieResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityConfirmRentalBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.exoplayer.ExoPlayerActivity
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toJson
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmRentalActivity : BaseActivity() {

    private lateinit var binding : ActivityConfirmRentalBinding
    private lateinit var movie: ContentDto
    private val viewModel: MoviesViewModel by viewModels()
    private var gradient: GradientDrawable? = null
    private var isCheckedIn = false
    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    override fun observeViewModel() {
        observe(viewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(viewModel.rentalMovieResponse, ::handleMovieResponse)
        observe(viewModel.themeLiveData, ::handleThemeResponse)
        observeToast(viewModel.showToast)

        viewModel.getThemeResponseData(themeDataStore)
    }

    override fun initViewBinding() {
        binding = ActivityConfirmRentalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        viewModel.validateSession(preferenceDataStoreHelper)

        val data = intent.getStringExtra(Constants.MOVIE_RENTALS)
        movie =  Gson().fromJson(data, ContentDto::class.java)

        binding.tvMovieName.text = movie.movieName
        binding.tvPriceConfirm.text = getString(R.string.rental_price_confirm, "", movie.price.toString())

        binding.btnConfirm.setOnFocusChangeListener(::handleBackClick)
        binding.btnCancel.setOnFocusChangeListener(::handleBackClick)

        binding.btnConfirm.setOnClickListener {
            val request = RentalMovieRequest()
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

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnCancel.requestFocus()

    }

    private fun handleBackClick(view: View, focus: Boolean) {
        if (focus) {
            view.background = getGradient(gradientStartColor, gradientEndColor)
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }

    private fun handleMovieResponse(state: Resource<RentalMovieResponse>) {
        when (state) {
            is Resource.Loading -> {

            }

            is Resource.Success -> {
                    state.data?.let { data ->
                        when (data.errorCode) {
                            0 -> {
                                startActivity(data)
                            }

                            1 -> viewModel.showToastMessage(getString(R.string.product_is_currently_unavailable))
                            2 -> viewModel.showToastMessage(getString(R.string.please_contact_the_front_desk_for_assistance))
                            3 -> viewModel.showToastMessage(getString(R.string.call_front_desk_to_activate_moviebeam_services))
                            else -> {

                            }
                        }
                    }

            }

            else -> {
                viewModel.showToastMessage(state.errorMsg.toString())
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                viewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                viewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }

                gradient = getGradient(gradientStartColor, gradientEndColor)

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

    private fun getGradient(startColor: String, endColor: String): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )

        gradientDrawable.cornerRadius = 20f

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
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
    }


}