package com.diipl.moviebeam.ui.weather

import com.diipl.moviebeam.databinding.ActivityWeatherBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : BaseActivity() {

    private lateinit var binding: ActivityWeatherBinding

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { handleBackClick() }
        setContentView(binding.root)
    }
    fun handleBackClick() = finish()

}