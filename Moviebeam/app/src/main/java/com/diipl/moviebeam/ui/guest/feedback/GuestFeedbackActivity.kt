package com.diipl.moviebeam.ui.guest.feedback

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.feedback.FeedbackResponse
import com.diipl.moviebeam.databinding.ActivityGuestFeedbackBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.feedback.FeedbackViewModel
import com.diipl.moviebeam.ui.guestservice.feedback.thankyou.ThankYouActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestFeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityGuestFeedbackBinding
    private val feedbackViewModel: FeedbackViewModel by viewModels()
    private var lastFocusedStar: ImageView? = null

    override fun observeViewModel() {
        observe(feedbackViewModel.feedbackLiveData, ::handleFeedbackResponse)
    }

    override fun initViewBinding() {
        binding = ActivityGuestFeedbackBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { handleBackClick() }

        binding.btnBack.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        lastFocusedStar?.requestFocus()
                    }
                    KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }

        setContentView(binding.root)
        setupUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ivStar1.requestFocus()
        binding.ivStar2.requestFocus()
        binding.ivStar3.requestFocus()
        binding.ivStar4.requestFocus()
        binding.ivStar5.requestFocus()
    }

    private fun setupUI() {
        binding.ivStar1.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.UNACCEPTABLE,
                    Color.parseColor(Constants.FEEDBACK_NEGATIVE_COLOR)
                )
                lastFocusedStar = view
            } else {
                view.setOnKeyListener { _, keycode, keyEvent ->
                    if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keycode) {
                            KeyEvent.KEYCODE_DPAD_LEFT -> binding.btnBack.requestFocus()
                        }
                    }
                    false
                }
            }
            animate(view, isFocused)
        }
        binding.ivStar2.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.DISAPPOINTING,
                    Color.parseColor(Constants.FEEDBACK_NEGATIVE_COLOR)
                )
                lastFocusedStar = view
            } else {
                setLeftKeyListener(view)
            }
            animate(view, isFocused)
        }
        binding.ivStar3.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.AVERAGE,
                    Color.parseColor(Constants.FEEDBACK_NEGATIVE_COLOR)
                )
                lastFocusedStar = view

            } else {
                setLeftKeyListener(view)
            }
            animate(view, isFocused)
        }
        binding.ivStar4.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.GOOD,
                    Color.parseColor(Constants.FEEDBACK_POSITIVE_COLOR)
                )
                lastFocusedStar = view

            } else {
                setLeftKeyListener(view)
            }
            animate(view, isFocused)
        }
        binding.ivStar5.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.EXCELLENT,
                    Color.parseColor(Constants.FEEDBACK_NEGATIVE_COLOR)
                )
                lastFocusedStar = view
                setLeftKeyListener(view)
            } else {
                setLeftKeyListener(view)
            }
            animate(view, isFocused)
        }
        binding.ivStar5.setOnClickListener {
            sendFeedback(Constants.EXCELLENT)
        }
        binding.ivStar4.setOnClickListener {
            sendFeedback(Constants.GOOD)
        }
        binding.ivStar3.setOnClickListener {
            sendFeedback(Constants.AVERAGE)
        }
        binding.ivStar2.setOnClickListener {
            sendFeedback(Constants.DISAPPOINTING)
        }
        binding.ivStar1.setOnClickListener {
            sendFeedback(Constants.UNACCEPTABLE)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setFocus(imageView: ImageView, feedback: String, feedbackColor: Int) {
        imageView.setImageResource(R.drawable.selected_feedback_icon)
        binding.tvFeedback.text = feedback
        binding.tvFeedback.setTextColor(feedbackColor)
    }

    private fun removeFocus(imageView: ImageView) {
        imageView.setImageResource(R.drawable.unselected_feedback_icon)
    }

    private fun setLeftKeyListener(view: View) {
        view.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        removeFocus(view as ImageView)
                    }
                }
            }
            false
        }
    }

    private fun animate(view: View, isFocused: Boolean) {
        val anim: Animation = if (isFocused) {
            AnimationUtils.loadAnimation(this, R.anim.scale_in_animation_feedback)
        } else {
            AnimationUtils.loadAnimation(this, R.anim.scale_out_animation_feedback)
        }
        view.startAnimation(anim)
        anim.fillAfter = true
    }

    private fun handleFeedbackResponse(status: Resource<FeedbackResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val intent = Intent(this, ThankYouActivity::class.java)
                startActivity(intent)
                binding.pbLoader.toInvisible()
                finish()
            }
            else -> {
                status.errorCode?.let { feedbackViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun sendFeedback(feedback: String) = feedbackViewModel.sendGuestFeedback(feedback)

    fun handleBackClick() = finish()

}
