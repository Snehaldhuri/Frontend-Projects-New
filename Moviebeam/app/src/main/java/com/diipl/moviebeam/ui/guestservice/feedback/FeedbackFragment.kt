package com.diipl.moviebeam.ui.guestservice.feedback

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.feedback.FeedbackResponse
import com.diipl.moviebeam.databinding.FragmentFeedbackBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.guestservice.feedback.thankyou.ThankYouActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class FeedbackFragment(
    private val onLeftKeyPressed: () -> Unit
) : BaseFragment() {

    private var _binding: FragmentFeedbackBinding? = null
    val binding get() = _binding!!
    private val feedbackViewModel: FeedbackViewModel by activityViewModels()

    override fun observeViewModel() {
        observe(feedbackViewModel.feedbackLiveData, ::handleFeedbackResponse)
    }

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        binding.ivStar1.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.UNACCEPTABLE,
                    Color.parseColor(Constants.FEEDBACK_NEGATIVE_COLOR)
                )
            } else {
                view.setOnKeyListener { _, keycode, keyEvent ->
                    if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                        when (keycode) {
                            KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
                        }
                    }
                    false
                }
            }
            animate(view, isFocused)
        }
        binding.ivStar1.requestFocus()
        binding.ivStar2.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.DISAPPOINTING,
                    Color.parseColor(Constants.FEEDBACK_NEGATIVE_COLOR)
                )
            } else {
                setLeftKeyListener(view)
            }
            animate(view, isFocused)
        }
        binding.ivStar3.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.GOOD,
                    Color.parseColor(Constants.FEEDBACK_POSITIVE_COLOR)
                )
            } else {
                setLeftKeyListener(view)
            }
            animate(view, isFocused)
        }
        binding.ivStar4.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                setFocus(
                    (view as ImageView),
                    Constants.EXCELLENT,
                    Color.parseColor(Constants.FEEDBACK_POSITIVE_COLOR)
                )
            } else {
                removeFocus(view as ImageView)
            }
            animate(view, isFocused)
        }
        binding.ivStar1.setOnClickListener {
            sendFeedback(Constants.UNACCEPTABLE)
        }
        binding.ivStar2.setOnClickListener {
            sendFeedback(Constants.DISAPPOINTING)
        }
        binding.ivStar3.setOnClickListener {
            sendFeedback(Constants.GOOD)
        }
        binding.ivStar4.setOnClickListener {
            sendFeedback(Constants.EXCELLENT)
        }
        return binding.root
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
        var anim: Animation =
            AnimationUtils.loadAnimation(context, R.anim.scale_out_animation_feedback)
        if (isFocused) {
            anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_animation_feedback)
        }
        view.startAnimation(anim)
        anim.fillAfter = true
    }

    private fun handleFeedbackResponse(status: Resource<FeedbackResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val intent = Intent(binding.root.context, ThankYouActivity::class.java)
                startActivity(intent)
                binding.pbLoader.toInvisible()
                activity?.finish()
            }

            else -> {
                status.errorCode?.let { feedbackViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun sendFeedback(feedback: String) {
        feedbackViewModel.sendGuestFeedback(Constants.UA, feedback)
    }

}