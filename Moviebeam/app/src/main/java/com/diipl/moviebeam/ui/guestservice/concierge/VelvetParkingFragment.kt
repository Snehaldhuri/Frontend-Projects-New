package com.diipl.moviebeam.ui.guestservice.concierge

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentVelvetParkingBinding


class VelvetParkingFragment : Fragment() {



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
            if(hasFocus){
                setVelvetFocus(binding.edtTicketNo)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        Log.d("keypressed", "keypressed")
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                binding.btnOk.requestFocus()
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }
            }
            else{
                binding.edtTicketNo.setBackgroundResource(R.color.white)
            }
        }
        binding.btnOk.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus){
                setFocus(binding.btnOk)
            }
            else{
                binding.btnOk.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnCancel.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus){
                setFocus(binding.btnCancel)
            }
            else{
                binding.btnCancel.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        binding.btnOk.setOnClickListener(View.OnClickListener {
            layout_velvet_parking_number.visibility = View.GONE
            layout_confirmation.visibility = View.VISIBLE

            binding.tvMessage.text =
                "Thank you.Your request has been sent .Please proceed with valet desk to retrive your vehicle"

        })

        return binding.root
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
    private fun setVelvetFocus(cardView: EditText) {
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