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
import java.util.Calendar


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
//        layout_dt = binding.root.findViewById(R.id.layout_dt)
//        layout_confirmation = binding.root.findViewById(R.id.layout_confirmation)



        return binding.root
    }






}