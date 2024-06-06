package com.diipl.moviebeam.ui.inroomdining

import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.databinding.ActivityInRoomDiningBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InRoomDiningActivity : BaseActivity() {

    private lateinit var binding: ActivityInRoomDiningBinding

    private val inRoomDiningViewModel: InRoomDiningViewModel by viewModels()

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>


    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        binding = ActivityInRoomDiningBinding.inflate(layoutInflater)
        val view = binding.root
        fetchDetails()
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_in_room_dining)

        inRoomDiningViewModel.getThemeResponseData(themeDataStore)
        LoggingService.sendMessageToWebSocket(
            "In InRoomDiningMM activity",
            getCurrentPanelNumber()
        )

        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun fetchDetails() {
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
//        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")

        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        binding.root.loadBg()
    }

}