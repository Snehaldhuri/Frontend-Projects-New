package com.diipl.moviebeam.ui.guestservice.concierge


import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.FragmentGolfBinding


class GolfFragment(
    private var onOkClicked: () -> Unit
) : Fragment() {

    private var _binding: FragmentGolfBinding? = null
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
        _binding = FragmentGolfBinding.inflate(inflater, container, false)
        layout_dt = binding.root.findViewById(R.id.layout_dt)
        layout_confirmation = binding.root.findViewById(R.id.layout_confirmation)

        binding.btnCancel.setOnClickListener(View.OnClickListener {
            onOkClicked()
        })

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
            layout_dt.visibility = View.GONE
            layout_confirmation.visibility = View.VISIBLE

            binding.btnPopOk.postDelayed({
                binding.btnPopOk.requestFocus()
            }, 1)

            binding.tvMessage.text =
                "Thank you.Your request has been received and your room will be serviced on " + day + ". " + month + " " + date + " " + year + " at " + currentHour + ":" + currentminute
            binding.btnPopOk.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    setFocus(binding.btnPopOk)
                } else {
                    binding.btnPopOk.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                }
            }
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


        val hourPicker = binding.layoutDateTimeSelector.timeSelectorLayout.hourPicker
        val minutePicker = binding.layoutDateTimeSelector.timeSelectorLayout.minutePicker

        hourPicker.requestFocus()
        hourPicker.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.setBackgroundResource(R.drawable.border_bg)
                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        Log.d("keypressed", "keypressed")
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
                        Log.d("keypressed", "keypressed")
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

    fun setDate(
        hour: String,
        minute: String,
        day: String,
        date: String,
        month: String,
        year: String
    ) {
        this.currentHour = hour.toInt()
        this.currentminute = minute.toInt()
        this.day = day.uppercase()
        this.date = date
        this.month = getMonth(month.toInt())
        this.year = year
        Log.d("setDatedate", "onCreateView:${day + month + date + currentHour} ")
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
        return months[mon - 1]
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

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }

}