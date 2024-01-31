package com.diipl.moviebeam.ui.guestservice.localAttraction

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.localattraction.LocalAttractionResponse
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.FragmentLocalattractionBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocalAttractionGsFragment : BaseFragment() {

    private lateinit var binding: FragmentLocalattractionBinding
    private var gradientStartColor: String? = null
    private var gradientEndColor: String? = null

    private val localAttractionViewModel: LocalAttractionGsViewModel by viewModels()

    @Inject
    lateinit var themeDataStore: DataStore<ThemeResponse>

    @Inject
    lateinit var weatherDataStore: DataStore<WeatherResponse>

    @Inject
    lateinit var localAttractionDataStore: DataStore<LocalAttractionResponse>

    override fun observeViewModel() {
        observe(localAttractionViewModel.localAttractionLiveData, ::handleLAServiceResponse)
    }

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocalattractionBinding.inflate(inflater, container, false)
        localAttractionViewModel.getLocalAttractionResponseData(localAttractionDataStore)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val cardRecyclerView: RecyclerView = binding.laCardCarousel
        cardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun handleLAServiceResponse(status: Resource<LocalAttractionResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val response = localAttractionViewModel.localAttractionLiveData.value?.data

                localAttractionViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                localAttractionViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                val adapter = LocalAttractionGsAdapter {
                    val cardAdapter = LaCardAdapterGs {

                    }
                    cardAdapter.setList(it.serviceList)
                    cardAdapter.setGradientDrawable(
                        getGradient(

                        )
                    )
                    binding.laCardCarousel.adapter = cardAdapter
                }

                adapter.setItemList(response?.servicesList!!)
                adapter.setGradientDrawable(getGradient())
                binding.recyclerView.adapter = adapter
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { localAttractionViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    fun setGradientColor(startColor: String, endColor: String) {
        gradientStartColor = startColor
        gradientEndColor = endColor
    }

    private fun getGradient(
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 10f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }


}
