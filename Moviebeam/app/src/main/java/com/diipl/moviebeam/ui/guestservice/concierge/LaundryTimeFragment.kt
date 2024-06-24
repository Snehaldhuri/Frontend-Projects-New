package com.diipl.moviebeam.ui.guestservice.concierge


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentLaundryTimeBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import java.util.Calendar


class LaundryTimeFragment(
    private var onOkClicked: () -> Unit
) : Fragment() {

    private var _binding: FragmentLaundryTimeBinding? = null
    val binding get() = _binding!!

    private var day: String = ""
    private var date: String = ""
    private var month: String = ""
    private var year: String = ""
    private var currentHour: Int = 24
    private var currentminute: Int = 60
    lateinit var layout_dt: LinearLayout
    lateinit var layout_confirmation: LinearLayout

    private var startColor = ""
    private var endColor = ""

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLaundryTimeBinding.inflate(inflater, container, false)
        layout_dt = binding.root.findViewById(R.id.layout_dt)
        setDate()
        layout_confirmation = binding.root.findViewById(R.id.layout_confirmation)

        val hourPicker = binding.layoutDateTimeSelector.timeSelectorLayout.hourPicker
        val minutePicker = binding.layoutDateTimeSelector.timeSelectorLayout.minutePicker

        hourPicker.post {
            hourPicker.requestFocus()
        }
        binding.btnCancel.setOnClickListener { onOkClicked() }

        binding.btnOk.handleFocusChange()
        binding.btnCancel.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.background = getGradientColor()
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                hourPicker.requestFocus()
                                return@setOnKeyListener true
                            }

                        }
                    }
                    false
                }
            } else {
                binding.btnCancel.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnOk.setOnClickListener(View.OnClickListener {
            layout_dt.visibility = View.GONE
            layout_confirmation.visibility = View.VISIBLE

            binding.btnPopOk.postDelayed({
                binding.btnPopOk.requestFocus()
            }, 1)

            binding.tvMessage.text =
                "Thank you.Your request has been received and your room will be serviced on " + day + ". " + month + " " + date + " " + year + " at " + currentHour + ":" + currentminute
            binding.btnPopOk.handleFocusChange()
            binding.btnPopOk.setOnClickListener {
                onOkClicked()
            }

        })

        binding.layoutDateTimeSelector.tvDay.text = day
        binding.layoutDateTimeSelector.tvDate.text = "$date / $month / $year"

        binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
            currentHour.toString()

        binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedMinute.text =
            currentminute.toString()


        hourPicker.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.setBackgroundResource(R.drawable.border_bg)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                if (currentHour < 24) {
                                    currentHour++
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
                                        currentHour.toString()
                                }
                                return@setOnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                if (currentHour > 1) {
                                    currentHour--
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
                                        currentHour.toString()
                                }
                                return@setOnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                binding.btnOk.requestFocus()
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }

            } else {
                view.setBackgroundResource(R.color.transparent)
            }
        }
        minutePicker.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.setBackgroundResource(R.drawable.border_bg)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                if (currentminute < 60) {
                                    currentminute++
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedMinute.text =
                                        currentminute.toString()
                                }
                                if (currentminute == 60) {
                                    currentminute = 1
                                    currentHour++
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
                                        currentHour.toString()
                                }
                                return@setOnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                if (currentminute >= 1) {
                                    currentminute--
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedMinute.text =
                                        currentminute.toString()
                                }
                                if (currentminute == 0) {
                                    currentminute = 59
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedMinute.text =
                                        currentminute.toString()
                                    currentHour--
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
                                        currentHour.toString()
                                }
                                return@setOnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_CENTER -> {
                                binding.btnOk.requestFocus()
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }

            } else {
                view.setBackgroundResource(R.color.transparent)
            }
        }
        return binding.root
    }

    private fun setDate() {
        val cal = Calendar.getInstance()
        this.currentHour = cal.get(Calendar.HOUR_OF_DAY)
        this.currentminute = cal.get(Calendar.MINUTE)
        this.day = getDay(cal.get(Calendar.DAY_OF_WEEK_IN_MONTH))
        this.date = cal.get(Calendar.DATE).toString()
        this.month = getMonth(cal.get(Calendar.MONTH))
        this.year = cal.get(Calendar.YEAR).toString()
    }

    private fun getDay(day: Int): String {
        return when (day) {
            1 -> "SUN"
            2 -> "MON"
            3 -> "TUE"
            4 -> "WED"
            5 -> "THU"
            6 -> "FRI"
            else -> "SAT"
        }
    }

    private fun getMonth(mon: Int): String {
        val months = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        return months[mon]
    }

}