package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.databinding.FragmentLaundryRequestSummaryBinding
import com.diipl.moviebeam.ui.base.BaseFragment

class LaundryRequestSummaryFragment(private var onOkClicked: () -> Unit) : BaseFragment() {

    private var _binding: FragmentLaundryRequestSummaryBinding? = null
    val binding get() = _binding
    private var gradientStartColor = ""
    private var gradientEndColor = ""
    private val adapter = LaundryRequestSummaryAdapter()
    private var selectedItems: MutableList<LaundryResponce> = mutableListOf()
    override fun observeViewModel() {
    }

    override fun initViewBinding() {
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laundry_request_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnConfirm?.setOnClickListener {
            onOkClicked()
        }
        binding?.btnConfirm?.postDelayed({
            binding?.btnConfirm!!.requestFocus()
        }, 1)

        binding?.btnConfirm?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(binding!!.btnConfirm)
            } else {
                binding!!.btnConfirm.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding?.rvSummary?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter.setItemList(selectedItems)
        binding?.rvSummary?.adapter = adapter
    }

    fun setItemList(selectedItems: MutableList<LaundryResponce>) {
        this.selectedItems = selectedItems
    }

    private fun setFocus(cardView: Button) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        cardView.background = gradientDrawable
    }
    fun setGradientColor(startColor: String, endColor: String) {
        this.gradientStartColor = startColor
        this.gradientEndColor = endColor
    }

}