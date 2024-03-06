package com.diipl.moviebeam.ui.inroomdining

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.databinding.ActivityInRoomDiningBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InRoomDiningActivity : BaseActivity() {

    private lateinit var binding: ActivityInRoomDiningBinding

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR
    private var gradient: GradientDrawable? = null

    private val inRoomDiningViewModel: InRoomDiningViewModel by viewModels()
    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>


    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        binding = ActivityInRoomDiningBinding.inflate(layoutInflater)
        val view = binding.root
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        fetchDetails()
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_in_room_dining)

        inRoomDiningViewModel.getThemeResponseData(themeDataStore)
        LoggingService.sendMessageToWebSocket("In InRoomDiningMM activity")

        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnBack.setOnClickListener{
           finish()
        }
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

    private fun getGradient(): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )

        gradientDrawable.cornerRadius = 20f

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun fetchDetails() {
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        intent.extras?.getString("gradientStartColor")?.let {
            gradientStartColor = it
        }
        intent.extras?.getString("gradientEndColor")?.let {
            gradientEndColor = it
        }
        gradient = getGradient()
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        loadBg(intent.extras?.getString("themeBackgroundFileName"))
    }

}