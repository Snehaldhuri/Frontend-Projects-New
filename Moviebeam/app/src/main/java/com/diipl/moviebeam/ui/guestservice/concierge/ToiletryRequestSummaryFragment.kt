package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
import com.diipl.moviebeam.databinding.FragmentToiletryRequestSummaryBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.handleFocusChange


class ToiletryRequestSummaryFragment(
    private var onOkClicked: () -> Unit
) : BaseFragment() {

    private var _binding: FragmentToiletryRequestSummaryBinding? = null
    val binding get() = _binding!!
    private val adapter = ToiletryRequestSummaryAdapter()
    private var selectedItems: List<ToiletryResponse.ToiletryData> = mutableListOf()
    override fun observeViewModel() {}

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToiletryRequestSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            onOkClicked()
        }
        binding.btnCancel.setOnClickListener {
            onOkClicked()
        }
        binding.btnConfirm.postDelayed({
            binding.btnConfirm.requestFocus()
        }, 1)

        binding.btnConfirm.handleFocusChange()
        binding.btnCancel.handleFocusChange()
        binding.rvSummary.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter.setItemList(selectedItems)
        binding.rvSummary.adapter = adapter
    }

    fun setItemList(selectedItems: List<ToiletryResponse.ToiletryData>) {
        this.selectedItems = selectedItems
    }

}
