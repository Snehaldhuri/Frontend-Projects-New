package com.diipl.moviebeam.ui.hotelinfo

import android.os.Bundle
import com.diipl.moviebeam.databinding.ActivityHelpInfoBinding
import com.diipl.moviebeam.ui.base.BaseActivity

class HelpInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityHelpInfoBinding
    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityHelpInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnBack.setOnClickListener{
            finish()
        }
    }
}