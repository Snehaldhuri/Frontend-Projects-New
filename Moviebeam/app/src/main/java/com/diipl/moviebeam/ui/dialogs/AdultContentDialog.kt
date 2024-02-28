package com.diipl.moviebeam.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.DialogAdultContentBinding
import com.diipl.moviebeam.utils.Constants.ADULT_CONTENT_DISABLED
import com.diipl.moviebeam.utils.Constants.ADULT_LOCKED
import com.diipl.moviebeam.utils.Constants.ADULT_MCD_BTN
import com.diipl.moviebeam.utils.Constants.ADULT_MCW_BTN
import com.diipl.moviebeam.utils.Constants.ADULT_MCW_MAIN
import com.diipl.moviebeam.utils.SharedPreference
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdultContentDialog(
    val viewType: Int,
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

        
        when (viewType) {
            ADULT_MCW_MAIN -> {
                binding.layoutParentalMCW.toVisible()
                binding.btnParentalControl.requestFocus()
            }
            ADULT_MCW_BTN -> {
                binding.layoutMCW.toVisible()
                binding.btnContinue.requestFocus()
            }
            ADULT_CONTENT_DISABLED -> {
                binding.layoutDisabled.toVisible()
                binding.btnOk.requestFocus()
            }
            ADULT_MCD_BTN -> {
                binding.layoutParentalMCD.toVisible()
                binding.btnMcdParentalControl.requestFocus()
            }
            ADULT_LOCKED -> {
                binding.layoutLocked.toVisible()
                binding.btnEnterParentalCode.requestFocus()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback{
            Log.e("onViewCreated: ", "onBackPressedDispatcher")
        }

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



    }

    private fun handleFocusChange(view: View, focus: Boolean) {
        if (focus) {
            view.background = getGradientColor()
        } else {
            view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }


}