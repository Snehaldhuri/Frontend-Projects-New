package com.diipl.moviebeam.ui.guestservice.localAttraction

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
                val response = status.data
                val adapter = LocalAttractionGsAdapter(onItemClicked = { view, it ->
                    binding.laCardCarousel.toInvisible()
                    val cardAdapter = LaCardAdapterGs { v ->
                        lastView = v
                        binding.recyclerView.post {
                            binding.recyclerView.findContainingItemView(view)?.requestFocus()
                        }
                    }
                    cardAdapter.setList(it.serviceList)
                    binding.laCardCarousel.post { binding.laCardCarousel.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus() }
                    binding.laCardCarousel.adapter = cardAdapter
                    binding.laCardCarousel.postDelayed({
                        binding.laCardCarousel.toVisible()
                    }, 240)
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
                }, onRightKeyClicked = { v ->
                    v.setOnKeyListener { _, i, _ ->
                        if (lastView != null && i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            binding.laCardCarousel.post {
                                lastView?.let {
                                    binding.laCardCarousel.findContainingItemView(it)
                                        ?.requestFocus()
                                }
                            }
                        }
                        false
                    }
                }
                )

                adapter.setItemList(response?.servicesList!!)
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

}
