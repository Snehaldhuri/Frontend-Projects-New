package com.diipl.moviebeam.ui.guestservice.concierge

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentVelvetParkingBinding
import com.diipl.moviebeam.utils.showKeyboard


class VelvetParkingFragment(
    private var onOkClicked: () -> Unit
) : Fragment() {


    private var _binding: FragmentVelvetParkingBinding? = null
    val binding get() = _binding!!
    lateinit var layout_velvet_parking_number: LinearLayout
    lateinit var layout_confirmation: LinearLayout

    private var startColor = ""
    private var endColor = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentVelvetParkingBinding.inflate(inflater, container, false)
        layout_velvet_parking_number = binding.root.findViewById(R.id.layout_velvet_parking_number)
        layout_confirmation = binding.root.findViewById(R.id.layout_confirmation)

        binding.edtTicketNo.requestFocus()
        binding.edtTicketNo.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
//                setVelvetFocus(view)
//                showSerialNumberDialog()
                view.showKeyboard()
            } else {
                binding.edtTicketNo.setBackgroundResource(R.drawable.rounded_corner_border)
            }
        }
        binding.btnOk.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(binding.btnOk)
            } else {
                binding.btnOk.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnCancel.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(binding.btnCancel)
            } else {
                binding.btnCancel.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        binding.btnOk.setOnClickListener(View.OnClickListener {
            layout_velvet_parking_number.visibility = View.GONE
            layout_confirmation.visibility = View.VISIBLE

            binding.btnPopOk.postDelayed({
                binding.btnPopOk.requestFocus()
            }, 1)

            binding.btnPopOk.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    setFocus(binding.btnPopOk)
                } else {
                    binding.btnPopOk.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                }
            }
            binding.tvMessage.text =
                "Thank you.Your request has been sent .Please proceed with valet desk to retrive your vehicle"

            binding.btnPopOk.setOnClickListener {
                onOkClicked()
            }
        })

        return binding.root
    }

    private fun showSerialNumberDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Enter Velvet ticket Number")

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

    private fun setFocus(cardView: Button) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        cardView.background = gradientDrawable
    }

    private fun setVelvetFocus(cardView: View) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        cardView.background = gradientDrawable
    }

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }
}