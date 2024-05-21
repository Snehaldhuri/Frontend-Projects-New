package com.diipl.moviebeam.ui.guestservice.concierge

import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
        binding.edtTicketNo.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.showKeyboard()

                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                binding.btnOk.requestFocus()
                                return@setOnKeyListener true
                            }

                        }
                    }
                    false
                }
            } else {
                view.hideKeyboard()
                binding.edtTicketNo.setBackgroundResource(R.drawable.rounded_corner_border)
            }
        }
        binding.btnOk.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.handleFocusChange()
                view.setOnKeyListener { _, keyCode, event ->
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
            } else {
                binding.btnOk.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnCancel.handleFocusChange()
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

            binding.btnPopOk.setOnClickListener {
                onOkClicked()
            }
        }

        return binding.root
    }

    private fun showSerialNumberDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Enter valet ticket Number")

        // Serial No :- 29221HFGN30WLA

        val input = EditText(context)
        var m_Text: String
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            m_Text = input.text.toString()
            binding.edtTicketNo.setText(m_Text)
            binding.btnOk.requestFocus()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
        }

        builder.show()
    }

}