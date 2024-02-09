package com.diipl.moviebeam.ui.guestservice.concierge

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentToiletryRequestErrorBinding
import com.diipl.moviebeam.databinding.FragmentToiletryRequestSummaryBinding
import com.diipl.moviebeam.ui.base.BaseFragment


class ToiletryRequestErrorFragment : BaseFragment() {

    private var _binding: FragmentToiletryRequestErrorBinding? = null
    val binding get() = _binding!!
    private var gradientStartColor = ""
    private var gradientEndColor = ""
    override fun observeViewModel() {

    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToiletryRequestErrorBinding.inflate(inflater, container, false)
        arguments?.let {

            gradientStartColor = it.getString("gradientStartColor").toString()
            gradientEndColor = it.getString("gradientEndColor").toString()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOk.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnOk.postDelayed({
            binding.btnOk.requestFocus()
        }, 1)

        binding.btnOk.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(binding.btnOk)
            } else {
                binding.btnOk.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
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