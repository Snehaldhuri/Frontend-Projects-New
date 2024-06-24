package com.diipl.moviebeam.ui.guestservice.concierge


import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentMakeMyRoomBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import java.util.Calendar


class MakeMyRoomFragment(
    private var onOkClicked: () -> Unit
) : Fragment() {

    private var _binding: FragmentMakeMyRoomBinding? = null
    val binding get() = _binding!!

    private var day: String = ""
    private var date: String = ""
    private var month: String = ""
    private var year: String = ""
    private var currentHour: Int = 24
    private var currentminute: Int = 60
    lateinit var layout_dt: LinearLayout
    lateinit var layout_confirmation: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMakeMyRoomBinding.inflate(inflater, container, false)
        this.setDate()
        layout_dt = binding.root.findViewById(R.id.layout_dt)
        layout_confirmation = binding.root.findViewById(R.id.layout_confirmation)

        view?.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_BACK -> {
                        onOkClicked()
                    }
                }
            }
            false
        }

        val hourPicker = binding.layoutDateTimeSelector.timeSelectorLayout.hourPicker
        val minutePicker = binding.layoutDateTimeSelector.timeSelectorLayout.minutePicker

        hourPicker.post {
            hourPicker.requestFocus()
        }


        binding.btnCancel.setOnClickListener {
            onOkClicked()
        }

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

        binding.layoutDateTimeSelector.tvDay.text = day
        binding.layoutDateTimeSelector.tvDate.text = "$date / $month / $year"

        val timeRanges = arrayOf("8 - 10 AM", "10 - 12 PM", "12 - 2 PM", "2 - 4 PM")
        var currentRangeIndex = 0

        binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
            timeRanges[currentRangeIndex]

        binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedMinute.text =
            currentminute.toString()

        hourPicker.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.setBackgroundResource(R.drawable.border_bg)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                if (currentRangeIndex < timeRanges.size - 1) {
                                    currentRangeIndex++
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
                                        timeRanges[currentRangeIndex]
                                    if (currentRangeIndex == timeRanges.size - 1) {
                                        binding.layoutDateTimeSelector.timeSelectorLayout.btnHourDown.visibility =
                                            View.INVISIBLE
                                        binding.layoutDateTimeSelector.timeSelectorLayout.btnHourUp.visibility =
                                            View.VISIBLE
                                    } else {
                                        binding.layoutDateTimeSelector.timeSelectorLayout.btnHourUp.visibility =
                                            View.VISIBLE
                                        binding.layoutDateTimeSelector.timeSelectorLayout.btnHourDown.visibility =
                                            View.VISIBLE
                                    }
                                }
                                return@setOnKeyListener true
                            }

                            KeyEvent.KEYCODE_DPAD_UP -> {

                                if (currentRangeIndex > 0) {
                                    currentRangeIndex--
                                    binding.layoutDateTimeSelector.timeSelectorLayout.tvSelectedHour.text =
                                        timeRanges[currentRangeIndex]

                                    binding.layoutDateTimeSelector.timeSelectorLayout.btnHourUp.visibility =
                                        View.VISIBLE
                                    binding.layoutDateTimeSelector.timeSelectorLayout.btnHourDown.visibility =
                                        View.VISIBLE
                                }
                                if (currentRangeIndex == 0) {
                                    binding.layoutDateTimeSelector.timeSelectorLayout.btnHourUp.visibility =
                                        View.INVISIBLE
                                    binding.layoutDateTimeSelector.timeSelectorLayout.btnHourDown.visibility =
                                        View.VISIBLE
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

        binding.btnOk.setOnClickListener(View.OnClickListener {
            layout_dt.visibility = View.GONE
            layout_confirmation.visibility = View.VISIBLE
            binding.btnPopOk.postDelayed({
                binding.btnPopOk.requestFocus()
            }, 1)

            binding.tvMessage.text =
                "Thank you.Your request has been received and your room will be serviced between " + timeRanges[currentRangeIndex] + " on " + day + ". " + month + " " + date + " " + year
            binding.btnPopOk.handleFocusChange()
            binding.btnPopOk.setOnClickListener {
                onOkClicked()
            }

        })

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
