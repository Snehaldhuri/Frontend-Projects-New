package com.diipl.moviebeam.ui.dialogs

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.DialogAdultContentBinding
import com.diipl.moviebeam.utils.Constants.ADULT_CONTENT_DISABLED
import com.diipl.moviebeam.utils.Constants.ADULT_LOCKED
import com.diipl.moviebeam.utils.Constants.ADULT_MCD_BTN
import com.diipl.moviebeam.utils.Constants.ADULT_MCW_BTN
import com.diipl.moviebeam.utils.Constants.ADULT_MCW_MAIN
import com.diipl.moviebeam.utils.Constants.PARENTAL_CONTROL
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AdultContentDialog"
@AndroidEntryPoint
class AdultContentDialog(
    var viewType: Int,
    private val onClicked: (Int) -> Unit
) : DialogFragment() {

    @Inject
    lateinit var preference: SharedPreference

    private lateinit var binding: DialogAdultContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogAdultContentBinding.inflate(inflater, container, false)

        binding.btnOk.setOnFocusChangeListener(::handleFocusChange)

        // MCW -> MAIN
        binding.btnParentalControl.setOnFocusChangeListener(::handleFocusChange)
        binding.btnSkip.setOnFocusChangeListener(::handleFocusChange)


        // MCW -> BTN
        binding.btnCancel.setOnFocusChangeListener(::handleFocusChange)
        binding.btnContinue.setOnFocusChangeListener(::handleFocusChange)

        // MCD -> BTN
        binding.btnMcdParentalControl.setOnFocusChangeListener(::handleFocusChange)
        binding.btnMcdOk.setOnFocusChangeListener(::handleFocusChange)

        // LOCKED -> BTN
        binding.btnEnterParentalCode.setOnFocusChangeListener(::handleFocusChange)
        binding.btnCodeCancel.setOnFocusChangeListener(::handleFocusChange)

        // PASSCODE
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

        val textSpan = SpannableString(getString(R.string.parental_control_desc) )
        val positionToPlaceImageAt = 18
        val image = getDrawable(requireContext(), R.drawable.img_dpad_navigation)
        image?.let {
            image.setBounds(0, 0, 24, 24)
            val imageSpan = ImageSpan(image, ImageSpan.ALIGN_CENTER)
            textSpan.setSpan(imageSpan, positionToPlaceImageAt - 1, positionToPlaceImageAt, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvImageDesc.text = textSpan
        }

        lifecycleScope.launch {
            delay(500)
            when (viewType) {
                ADULT_MCW_MAIN -> {
                    preference.isMainAdultMCW = true
                    binding.layoutParentalMCW.toVisible()
                    binding.btnParentalControl.requestFocus()
                }

                ADULT_MCW_BTN -> {
                    preference.isBtnAdultMCW = true
                    binding.layoutMCW.toVisible()
                    binding.btnContinue.requestFocus()
                }

                ADULT_CONTENT_DISABLED -> {
                    binding.layoutDisabled.toVisible()
                    binding.btnOk.requestFocus()
                }

                ADULT_MCD_BTN -> {
                    preference.isAdultMCD = true
                    binding.layoutParentalMCD.toVisible()
                    binding.btnMcdParentalControl.requestFocus()
                }

                ADULT_LOCKED -> {
                    binding.layoutLocked.toVisible()
                    binding.btnEnterParentalCode.requestFocus()
                }
                PARENTAL_CONTROL -> {
                    binding.layoutPassCode.toVisible()
                    binding.btnCodeOk.requestFocus()
                }
            }
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback {
            Log.e("onViewCreated: ", "onBackPressedDispatcher")
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOk.setOnClickListener {
            dismiss()
        }

        // MCW
        binding.btnCancel.setOnClickListener {
            preference.isBtnAdultMCW = true
            dismiss()
        }
        binding.btnContinue.setOnClickListener {
            preference.isBtnAdultMCW = true
            dismiss()
        }

        // MCW
        binding.btnSkip.setOnClickListener {
            preference.isMainAdultMCW = true
            dismiss()
        }
        binding.btnParentalControl.setOnClickListener {
            preference.isMainAdultMCW = true
            setParentalControlView()
        }

        // MCD
        binding.btnMcdOk.setOnClickListener {
            preference.isAdultMCD = true
            dismiss()
        }
        binding.btnMcdParentalControl.setOnClickListener {
            preference.isAdultMCD = true
            viewType = PARENTAL_CONTROL
            setParentalControlView()
        }

        // LOCKED
        binding.btnEnterParentalCode.setOnClickListener {
            setParentalControlView()
        }
        binding.btnCodeCancel.setOnClickListener {
            preference.isAdultLocked = true
            onClicked(0)
        }

    }

    private fun setParentalControlView() {
        when (viewType) {
            ADULT_MCW_MAIN -> {
                binding.layoutParentalMCW.toGone()
            }

            ADULT_MCW_BTN -> {
                binding.layoutMCW.toGone()
            }

            ADULT_CONTENT_DISABLED -> {
                binding.layoutDisabled.toGone()
            }

            ADULT_MCD_BTN -> {
                binding.layoutParentalMCD.toGone()
            }

            ADULT_LOCKED -> {
                binding.layoutLocked.toGone()
            }
        }

        binding.layoutPassCode.toVisible()

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

    private var length = 0
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
                if (pass.length < 4) {
                    showToast("Invalid password!")
                    clearView()
                } else if (!preference.isAdultPassCodeEmpty && pass != preference.adultPassCode) {
                    showToast("Password doesn't match.")
                    clearView()
                } else {
                    if (pass == preference.adultPassCode) {
                        preference.isAdultLocked = false
                        onClicked(1)
                        if (viewType == ADULT_MCD_BTN){
                            binding.layoutPassCode.toGone()
                            binding.layoutMCW.toVisible()
                            binding.btnContinue.requestFocus()
                            return
                        }
                    }
                    if (viewType == PARENTAL_CONTROL) {
                        preference.adultPassCode = pass
                        showToast("Passcode set successfully.")
                    }
                    dismiss()
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

    private fun clearView() {
        binding.etPass1.setText("")
        binding.etPass2.setText("")
        binding.etPass3.setText("")
        binding.etPass4.setText("")
        length = 0
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun handleFocusChange(view: View, focus: Boolean) {
        if (focus) {
            view.background = getGradientColor()
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }


}