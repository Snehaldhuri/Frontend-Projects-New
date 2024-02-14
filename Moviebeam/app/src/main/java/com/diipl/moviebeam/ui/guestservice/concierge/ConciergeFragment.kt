package com.diipl.moviebeam.ui.guestservice.concierge

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel
import com.diipl.moviebeam.databinding.FragmentConciergeBinding
import com.diipl.moviebeam.ui.guestservice.GuestServiceActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ConciergeFragment(private var onClick: (View, ConciergeBtnModel) -> Unit) : Fragment() {

    private lateinit var binding: FragmentConciergeBinding
    private var viewAdapt: View? = null
    private var cposition = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConciergeBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun setAdapter(
        conciergeModelList: List<ConciergeBtnModel>,
        gradientStartColor: String,
        gradientEndColor: String,
        positionView: View?
    ) {

        val conciergeAdapter = ConciergeAdapter { view, conciergeService ->
            onClick(view, conciergeService)
        }

        conciergeAdapter.setButtonList(conciergeModelList)
        conciergeAdapter.setGradientColor(gradientStartColor, gradientEndColor)
        lifecycleScope.launch {
            delay(500)
            binding.rvContent.adapter = conciergeAdapter
        }

        binding.rvContent.post {
            // Scroll to the desired position
            positionView?.let {
                val position = binding.rvContent.getChildAdapterPosition(it)
                binding.rvContent.findContainingItemView(it)?.requestFocus(position)
                cposition = position
                conciergeAdapter.getFocus(cposition)
            }

        }

    }
}