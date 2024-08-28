package com.diipl.moviebeam.ui.localattraction

import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.databinding.ActivityLocalAttractionBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocalAttractionActivity : BaseActivity() {
    private lateinit var binding: ActivityLocalAttractionBinding

    private val localAttractionViewModel: LocalAttractionViewModel by viewModels()

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var localAttractionDataStore: DataStore<LocalAttractionResponse>

    override fun observeViewModel() {
        observe(localAttractionViewModel.localAttractionLiveData, ::handleLAServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityLocalAttractionBinding.inflate(layoutInflater)
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // call below function to fetch data from dataStore

        localAttractionViewModel.getThemeResponseData(themeDataStore)
        localAttractionViewModel.getLocalAttractionResponseData(localAttractionDataStore)

        binding.btnBack.handleFocusChange()
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

                val adapter = LocalAttractionAdapter {
                    val cardAdapter = LaCardAdapter {

                    }
                    cardAdapter.setList(it.serviceList)
                    binding.laCardCarousel.adapter = cardAdapter
                }

                adapter.setItemList(response?.servicesList!!)
                binding.recyclerView.adapter = adapter
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { localAttractionViewModel.showToastMessage(getString(it)) }
            }
        }
    }

}
