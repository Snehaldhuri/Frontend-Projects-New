package com.diipl.moviebeam.ui.guestservice.feedback.thankyou

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.diipl.moviebeam.databinding.ActivityThankyouBinding
import com.diipl.moviebeam.ui.base.BaseActivity

class ThankYouActivity : BaseActivity() {

    private lateinit var binding: ActivityThankyouBinding
    private var gradient: GradientDrawable? = null

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityThankyouBinding.inflate(layoutInflater)
        fetchDetailsFromBundle()
        binding.btnOk.setOnFocusChangeListener { view, isFocused ->
            view.background = gradient
        }
        binding.btnOk.setOnClickListener {
            finish()
        }
        setContentView(binding.root)
    }

    private fun fetchDetailsFromBundle() {
        intent.extras?.let {
            setGradient(
                it.getString("gradientStartColor"),
                it.getString("gradientEndColor")
            )
        }
    }

    private fun setGradient(
        gradientStartColor: String?,
        gradientEndColor: String?
    ) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 10f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        this.gradient = gradientDrawable
    }
}