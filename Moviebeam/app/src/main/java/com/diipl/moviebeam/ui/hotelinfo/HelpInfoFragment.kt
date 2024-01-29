package com.diipl.moviebeam.ui.hotelinfo

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentHelpInfoBinding

class HelpInfoFragment(private var onBackButtonClick: () -> Unit) : Fragment() {

    private var _binding: FragmentHelpInfoBinding? = null
    private val binding get() = _binding!!

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpInfoBinding.inflate(inflater, container, false)
        arguments?.let {

            gradientStartColor = it.getString("gradientStartColor").toString()
            gradientEndColor = it.getString("gradientEndColor").toString()

        }
        binding.btnBack.post {
            binding.btnBack.requestFocus()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                view.background = getGradient(gradientStartColor, gradientEndColor)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                binding.rvHelpInfoHeader.post {
                                    binding.rvHelpInfoHeader.requestFocus()
                                }
                                binding.rvHelpInfoHeader.setOnFocusChangeListener { b, focus ->
                                    if (focus) {
                                        b.background =
                                            getGradient(gradientStartColor, gradientEndColor)
                                        b.setOnKeyListener { _, keyCode, event ->
                                            if (event.action == KeyEvent.ACTION_DOWN) {
                                                when (keyCode) {
                                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                                        binding.btnBack.post {
                                                            binding.btnBack.requestFocus()
                                                        }
                                                        return@setOnKeyListener true
                                                    }

                                                }
                                            }
                                            false
                                        }
                                    } else {
                                        b.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                                    }
                                }
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }

            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        binding.btnBack.setOnClickListener {
            handleBackClick()
        }

        val adapterForHelpInfo = HelpInfoTabAdapter(mutableListOf(Constants.SYSTEM_INFO))
        if (gradientStartColor.isNotEmpty() && gradientEndColor.isNotEmpty()) {
            adapterForHelpInfo.setGradientColor(gradientStartColor, gradientEndColor)
        }
        binding.rvHelpInfoHeader.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvHelpInfoHeader.adapter = adapterForHelpInfo

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

    private fun handleBackClick() {
        onBackButtonClick()
    }
}
