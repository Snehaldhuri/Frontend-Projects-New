package com.diipl.moviebeam.ui.dialogs

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentParentalControlBinding
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.getGradientColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ParentalControlFragment(private val onClicked: (Int) -> Unit) : Fragment() {

    private lateinit var binding: FragmentParentalControlBinding
    private var length = 0

    @Inject
    lateinit var preference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentalControlBinding.inflate(inflater, container, false)

        binding.btn1.setOnFocusChangeListener(::handleFocusChange)
        binding.btn2.setOnFocusChangeListener(::handleFocusChange)
        binding.btn3.setOnFocusChangeListener(::handleFocusChange)
        binding.btn4.setOnFocusChangeListener(::handleFocusChange)
        binding.btn5.setOnFocusChangeListener(::handleFocusChange)
        binding.btn6.setOnFocusChangeListener(::handleFocusChange)
        binding.btn7.setOnFocusChangeListener(::handleFocusChange)
        binding.btn8.setOnFocusChangeListener(::handleFocusChange)
        binding.btn9.setOnFocusChangeListener(::handleFocusChange)
        binding.btn0.setOnFocusChangeListener(::handleFocusChange)
        binding.btnDelete.setOnFocusChangeListener(::handleFocusChange)
        binding.btnCodeOk.setOnFocusChangeListener(::handleFocusChange)

        val textSpan = SpannableString(getString(R.string.parental_control_desc))
        val positionToPlaceImageAt = 18
        val image = AppCompatResources.getDrawable(requireContext(), R.drawable.img_dpad_navigation)
        image?.let {
            image.setBounds(0, 0, 24, 24)
            val imageSpan = ImageSpan(image, ImageSpan.ALIGN_CENTER)
            textSpan.setSpan(
                imageSpan,
                positionToPlaceImageAt - 1,
                positionToPlaceImageAt,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvImageDesc.text = textSpan
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.btnCodeOk.requestFocus()
        binding.btn1.setOnClickListener(::updateFields)
        binding.btn2.setOnClickListener(::updateFields)
        binding.btn3.setOnClickListener(::updateFields)
        binding.btn4.setOnClickListener(::updateFields)
        binding.btn5.setOnClickListener(::updateFields)
        binding.btn6.setOnClickListener(::updateFields)
        binding.btn7.setOnClickListener(::updateFields)
        binding.btn8.setOnClickListener(::updateFields)
        binding.btn9.setOnClickListener(::updateFields)
        binding.btn0.setOnClickListener(::updateFields)
        binding.btnCodeOk.setOnClickListener(::updateFields)
        binding.btnDelete.setOnClickListener(::updateFields)

    }

    private fun updateFields(it: View) {
        val btn: Button = it as Button
        when (btn.text.toString()) {
            "Del" -> {
                if (length == 4) {
                    binding.etPass4.setText("")
                    length--
                    return
                }
                if (length == 3) {
                    binding.etPass3.setText("")
                    length--
                    return
                }
                if (length == 2) {
                    binding.etPass2.setText("")
                    length--
                    return
                }
                if (length == 1) {
                    binding.etPass1.setText("")
                    length--
                    return
                }
            }

            "OK" -> {
                val pass =
                    binding.etPass1.text.toString() + binding.etPass2.text.toString() + binding.etPass3.text.toString() + binding.etPass4.text.toString()
                if (pass.length != 4) {
                    showToast("Invalid password!")
                    clearView()
                } else {
                    preference.adultPassCode = pass
                    showToast("Passcode set successfully.")
                    onClicked(RESULT_OK)
                }
            }

            else -> {
                val pass = btn.text.toString()
                if (length == 0) {
                    binding.etPass1.setText(pass)
                    length++
                    return
                }
                if (length == 1) {
                    binding.etPass2.setText(pass)
                    length++
                    return
                }
                if (length == 2) {
                    binding.etPass3.setText(pass)
                    length++
                    return
                }
                if (length == 3) {
                    binding.etPass4.setText(pass)
                    length++
                    return
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun clearView() {
        binding.etPass1.setText("")
        binding.etPass2.setText("")
        binding.etPass3.setText("")
        binding.etPass4.setText("")
    }


    private fun handleFocusChange(view: View, focus: Boolean) {
        if (focus) {
            view.background = getGradientColor()
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }
}