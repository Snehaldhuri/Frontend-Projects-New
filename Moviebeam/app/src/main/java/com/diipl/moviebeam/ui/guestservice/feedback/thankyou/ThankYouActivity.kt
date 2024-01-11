package com.diipl.moviebeam.ui.guestservice.feedback.thankyou

import com.diipl.moviebeam.databinding.ActivityThankyouBinding
import com.diipl.moviebeam.ui.base.BaseActivity

class ThankYouActivity : BaseActivity() {

    private lateinit var binding: ActivityThankyouBinding

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityThankyouBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}