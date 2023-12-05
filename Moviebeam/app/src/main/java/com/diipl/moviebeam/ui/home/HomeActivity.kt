package com.diipl.moviebeam.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diipl.moviebeam.databinding.ActivityHomeBinding
import com.diipl.moviebeam.databinding.SplashLayoutBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}