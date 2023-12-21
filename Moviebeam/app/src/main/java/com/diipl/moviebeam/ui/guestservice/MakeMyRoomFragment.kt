package com.diipl.moviebeam.ui.guestservice

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
//import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentMakeMyRoomBinding


class MakeMyRoomFragment : Fragment() {

    private var _binding: FragmentMakeMyRoomBinding? = null
    val binding get() = _binding!!

    private var day: String = ""
    private var date: String = ""
    private var month: String = ""
    private var year: String = ""
    private var currentHour: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMakeMyRoomBinding.inflate(inflater, container, false)

        binding.layoutDateTimeSelector.tvDay.text = day
        binding.layoutDateTimeSelector.tvDate.text = "$date / $month / $year"
        val hourPicker = binding.layoutDateTimeSelector.timeSelectorLayout.npHour
        hourPicker.minValue = 0
        hourPicker.maxValue = currentHour
        hourPicker.value = 0
        hourPicker.requestFocus()
        hourPicker.setOnFocusChangeListener { view, b ->
            if (b){
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }else{
                view.setBackgroundResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            }
        }

        val minutePicker = binding.layoutDateTimeSelector.timeSelectorLayout.npMinute
        val minuteValues = arrayOf("00", "15", "30", "45")
        minutePicker.minValue = 0
        minutePicker.maxValue = minuteValues.size - 1
        minutePicker.displayedValues = minuteValues
        minutePicker.setOnFocusChangeListener { view, b ->
            if (b){
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }else{
                view.setBackgroundResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
            }
        }
        return binding.root
    }

    fun setDate(hour: String, day: String, date: String, month: String, year: String){
        this.currentHour = hour.toInt()
        this.day = day.uppercase()
        this.date = date
        this.month = getMonth(month.toInt())
        this.year = year
    }

    private fun getMonth(mon: Int): String{
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return months[mon-1]
    }

}