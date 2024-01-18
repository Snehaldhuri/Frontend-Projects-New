package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diipl.moviebeam.databinding.FragmentToiletryRequestSummaryBinding
import com.diipl.moviebeam.ui.base.BaseFragment


class ToiletryRequestSummaryFragment : BaseFragment() {

    private var _binding: FragmentToiletryRequestSummaryBinding? = null
    val binding get() = _binding!!

    override fun observeViewModel() {

    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToiletryRequestSummaryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}