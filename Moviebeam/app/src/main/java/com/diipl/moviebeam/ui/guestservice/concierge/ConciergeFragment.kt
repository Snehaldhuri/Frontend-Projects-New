package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentConciergeBinding
import com.diipl.moviebeam.databinding.FragmentHotelServiceInfoBinding


class ConciergeFragment : Fragment() {
private var _binding: FragmentConciergeBinding? = null
    val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentConciergeBinding.inflate(inflater, container, false)
        binding.rvContent.layoutManager = GridLayoutManager(binding.root.context, 4)

//        val view = inflater.inflate(R.layout.fragment_concierge, container, false)
//        view.findViewById<>()
        return binding.root
    }


}