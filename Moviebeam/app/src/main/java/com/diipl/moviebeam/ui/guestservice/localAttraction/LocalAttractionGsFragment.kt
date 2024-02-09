package com.diipl.moviebeam.ui.guestservice.localAttraction

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.Resource
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
class LocalAttractionGsFragment(private var onItemClicked: (View) -> Unit) : BaseFragment() {

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
    private var lastView: View? = null

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
                val adapter = LocalAttractionGsAdapter(onItemClicked = { view, it ->
                    val cardAdapter = LaCardAdapterGs { v ->
                        lastView = v
                        binding.recyclerView.post {
                            binding.recyclerView.findContainingItemView(view)?.requestFocus()
                        }
                    }
                    cardAdapter.setList(it.serviceList)
                    cardAdapter.setGradientDrawable(
                        getGradient(

                        )
                    )
                    binding.laCardCarousel.adapter = cardAdapter
                    binding.laCardCarousel.post {
                        binding.laCardCarousel.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
                    }
                }, onLeftKeyClicked = { view ->
                    if (lastView == null)
                        onItemClicked(view)
                    else
                        view.setOnKeyListener { v, i, _ ->
                        if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                            onItemClicked(v)
                        }
                        false
                    }
                }, onRightKeyClicked = {v->
                    v.setOnKeyListener { _, i, _ ->
                        if (lastView != null && i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            binding.laCardCarousel.post {
                                lastView?.let {
                                    binding.laCardCarousel.findContainingItemView(it)?.requestFocus()
                                }
                            }
                        }
                        false
                    }
                }
                )

                adapter.setItemList(response?.servicesList!!)
                adapter.setGradientDrawable(getGradient())
                binding.recyclerView.adapter = adapter
                binding.loaderView.toInvisible()
                binding.recyclerView.post {
                    if (lastView == null)
                        binding.recyclerView.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
                    else
                        binding.recyclerView.findContainingItemView(lastView!!)?.requestFocus()
                }
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

    private fun getGradient(): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 16f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }


}
