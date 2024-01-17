package com.diipl.moviebeam.ui.hotelinfo

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentHelpInfoBinding

class HelpInfoFragment(private var onBackButtonClick: ((Boolean)) -> Unit) : Fragment() {

    private var _binding: FragmentHelpInfoBinding? = null
    private val binding get() = _binding!!

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpInfoBinding.inflate(inflater, container, false)
        arguments?.let {

            gradientStartColor = it.getString("gradientStartColor").toString()
            gradientEndColor = it.getString("gradientEndColor").toString()

        }

        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                binding.btnBack.background = getGradient(gradientStartColor, gradientEndColor)
            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            onBackButtonClick(true)
        }

        val adapterForHelpInfo = HelpInfoTabAdapter(mutableListOf(Constants.SYSTEM_INFO))
        if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
            adapterForHelpInfo.setGradientColor(gradientStartColor, gradientEndColor)
        }
        binding.rvHelpInfoHeader.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvHelpInfoHeader.adapter = adapterForHelpInfo
        return binding.root
    }

    private fun getGradient(startColor: String, endColor: String): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )

        gradientDrawable.cornerRadius = 20f

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }


}