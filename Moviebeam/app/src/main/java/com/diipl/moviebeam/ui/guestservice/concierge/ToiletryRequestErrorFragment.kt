package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diipl.moviebeam.databinding.FragmentToiletryRequestErrorBinding
import com.diipl.moviebeam.ui.base.BaseFragment


class ToiletryRequestErrorFragment : BaseFragment() {

    private var _binding: FragmentToiletryRequestErrorBinding? = null
    val binding get() = _binding!!

    override fun observeViewModel() {

    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToiletryRequestErrorBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOk.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}