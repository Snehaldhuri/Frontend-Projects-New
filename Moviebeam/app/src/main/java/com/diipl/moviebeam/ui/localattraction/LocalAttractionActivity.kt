package com.diipl.moviebeam.ui.localattraction

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.databinding.ActivityLocalAttractionBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LocalAttractionActivity : BaseActivity() {
    private lateinit var binding: ActivityLocalAttractionBinding
    private var gradientStartColor = "#85bf08"
    private var gradientEndColor = "#0ca654"

    private val localAttractionViewModel: LocalAttractionViewModel by viewModels()
    override fun observeViewModel() {
        observe(localAttractionViewModel.localAttractionLiveData, ::handleLAServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityLocalAttractionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                binding.btnBack.background = getGradient(gradientStartColor, gradientEndColor)
            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val cardRecyclerView: RecyclerView = binding.laCardCarousel
        cardRecyclerView.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cardRecyclerView: RecyclerView = binding.laCardCarousel
        cardRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun handleLAServiceResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val response = localAttractionViewModel.localAttractionLiveData.value?.data
                Glide.with(this)
                    .load(localAttractionViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.layoutHeader.ivHotelLogo)
                loadBg(localAttractionViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                val adapter = LocalAttractionAdapter {
                    val cardAdapter = LaCardAdapter {

                    }
                    cardAdapter.setList(it.serviceList)
                    cardAdapter.setGradientDrawable(
                        getGradient(
                            gradientStartColor,
                            gradientEndColor
                        )
                    )
                    binding.laCardCarousel.adapter = cardAdapter
                }
                adapter.setItemList(response?.servicesList!!)
                adapter.setGradientDrawable(getGradient(gradientStartColor, gradientEndColor))
                binding.recyclerView.adapter = adapter
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { localAttractionViewModel.showToastMessage(getString(it)) }
            }
        }
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

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    resource.alpha = 120
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}
