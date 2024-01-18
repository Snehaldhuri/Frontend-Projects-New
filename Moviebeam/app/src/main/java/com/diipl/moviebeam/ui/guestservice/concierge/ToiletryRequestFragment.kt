package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.databinding.FragmentToiletryRequestBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.guestservice.GuestServiceViewModel
import com.diipl.moviebeam.utils.SingleEvent
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.showToast
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import com.google.android.material.snackbar.Snackbar

class ToiletryRequestFragment : BaseFragment() {

    private var _binding: FragmentToiletryRequestBinding? = null
    val binding get() = _binding!!
    private var gradientStartColor = ""
    private var gradientEndColor = ""

    private val guestServiceViewModel: GuestServiceViewModel by activityViewModels()


    override fun observeViewModel() {
        observe(guestServiceViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
        observeToast(guestServiceViewModel.showToast)
    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToiletryRequestBinding.inflate(inflater, container, false)
        arguments?.let {

            gradientStartColor = it.getString("gradientStartColor").toString()
            gradientEndColor = it.getString("gradientEndColor").toString()
        }
        return binding.root
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> { binding.loaderView.toVisible() }
            is Resource.Success -> {
                val response = guestServiceViewModel.accountSetupLiveData.value?.data?.itemMenuList
                binding.rvToiletryRequest.layoutManager = LinearLayoutManager(requireActivity())
                val toiletryRequestAdapter = ToiletryRequestAdapter(){

                }

                response?.let { toiletryRequestAdapter.setButtonList(it) }
                toiletryRequestAdapter.setGradientColor(gradientStartColor, gradientEndColor)
                binding.rvToiletryRequest.adapter = toiletryRequestAdapter

                binding.loaderView.toInvisible()
            }
            else -> {
                status.errorMsg?.let { guestServiceViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        binding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


}