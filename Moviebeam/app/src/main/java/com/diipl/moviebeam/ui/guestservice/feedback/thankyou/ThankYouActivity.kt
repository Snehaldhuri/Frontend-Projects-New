package com.diipl.moviebeam.ui.guestservice.feedback.thankyou

import com.diipl.moviebeam.databinding.ActivityThankyouBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.handleFocusChange

class ThankYouActivity : BaseActivity() {

    private lateinit var binding: ActivityThankyouBinding

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityThankyouBinding.inflate(layoutInflater)
        binding.btnOk.handleFocusChange()
        binding.btnOk.setOnClickListener {
            finish()
        }
        setContentView(binding.root)
    }

}