package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentValetParkingBinding
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.hideKeyboard
import com.diipl.moviebeam.utils.showKeyboard
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible


class ValetParkingFragment(
    private var onOkClicked: () -> Unit
) : Fragment() {


    private var _binding: FragmentValetParkingBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentValetParkingBinding.inflate(inflater, container, false)

        binding.edtTicketNo.requestFocus()

        binding.btnCancel.handleFocusChange()
        binding.btnOk.handleFocusChange()
        binding.btnPopOk.handleFocusChange()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.edtTicketNo.setOnKeyListener { _, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    binding.btnOk.requestFocus()
                    return@setOnKeyListener true
                }

                KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_DOWN_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN_LEFT -> {
                    binding.btnCancel.requestFocus()
                    return@setOnKeyListener true
                }
            }
            false
        }

        binding.edtTicketNo.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.showKeyboard()
            } else {
                view.hideKeyboard()
                binding.edtTicketNo.setBackgroundResource(R.drawable.rounded_corner_border)
            }
        }

        binding.btnOk.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        binding.edtTicketNo.requestFocus()
                        return@setOnKeyListener true
                    }

                }
            }
            false
        }

        binding.btnCancel.setOnClickListener {
            onOkClicked()
        }
        binding.btnOk.setOnClickListener {
            binding.layoutVelvetParkingNumber.toGone()
            binding.layoutConfirmation.toVisible()

            binding.btnPopOk.postDelayed({
                binding.btnPopOk.requestFocus()
            }, 1)

            binding.btnPopOk.handleFocusChange()
            binding.tvMessage.text =
                "Thank you.your request has been sent.\nPlease proceed with valet desk to retrive your vehicle."

        }

        binding.btnPopOk.setOnClickListener {
            onOkClicked()
        }

    }

}