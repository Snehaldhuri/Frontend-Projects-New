package com.diipl.moviebeam.ui.appworld

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.databinding.ActivityAppWorldBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppWorldActivity : BaseActivity() {

    private lateinit var binding: ActivityAppWorldBinding
    private val appWorldViewModel: AppWorldViewModel by viewModels()

    override fun observeViewModel() {
        observe(appWorldViewModel.themeLiveData, ::handleThemeResponse)
    }

    override fun initViewBinding() {
        binding = ActivityAppWorldBinding.inflate(layoutInflater)
        val view = binding.root
        binding.rvApps.layoutManager = GridLayoutManager(this, 4)
        getInstalledApps()
        binding.btnBack.setOnClickListener {
            finish()
        }
        setContentView(view)
    }

    private fun getInstalledApps() {
        // get list of all the apps installed
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val adapter = AppAdapter {
            val i: Intent? = packageManager.getLaunchIntentForPackage(it.packageName)
            i?.let {
                startActivity(i)
            }
        }
        adapter.setAppList(apps)
        binding.rvApps.adapter = adapter
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                binding.btnBack.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        view.background = getGradient(
                            appWorldViewModel.themeLiveData.value?.data?.gradientColor,
                            appWorldViewModel.themeLiveData.value?.data?.gradientColor
                        )
                    } else {
                        view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                    }
                }
                appWorldViewModel.themeLiveData.value?.data?.themeLogoFileName?.let {
                    binding.layoutHeader.imgHotelLogo.loadImagesWithGlideExt(it)
                }
                loadBg(appWorldViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.btnBack.requestFocus()
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
            }
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

    private fun getGradient(startColor: String?, endColor: String?): GradientDrawable {
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

}