package com.diipl.moviebeam.ui.inroomdining

import android.os.Bundle
import com.diipl.moviebeam.databinding.ActivityInRoomDiningBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InRoomDiningActivity : BaseActivity() {

    private lateinit var binding: ActivityInRoomDiningBinding

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityInRoomDiningBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { finish() }
    }

}