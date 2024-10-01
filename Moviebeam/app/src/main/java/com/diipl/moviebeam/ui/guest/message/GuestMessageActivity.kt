package com.diipl.moviebeam.ui.guest.message

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.databinding.ActivityAppWorldBinding
import com.diipl.moviebeam.databinding.ActivityGuestMessageBinding
import com.diipl.moviebeam.databinding.ActivityInRoomDiningBinding
import com.diipl.moviebeam.databinding.FragmentMessageBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.feedback.FeedbackFragment
import com.diipl.moviebeam.ui.guestservice.message.MessageFragment
import com.diipl.moviebeam.ui.guestservice.message.MessageTabAdapter
import com.diipl.moviebeam.ui.guestservice.message.MessageViewModel
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuestMessageActivity : BaseActivity() {

    private lateinit var binding: ActivityGuestMessageBinding

    private val messageViewModel: MessageViewModel by viewModels()

    override fun observeViewModel() {
        observe(messageViewModel.guestMessageLiveData, ::handleGuestMessageResponse)
    }

    override fun initViewBinding() {
        binding = ActivityGuestMessageBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        binding.rvMessages.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun handleGuestMessageResponse(status: Resource<MessageResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                status.data?.let {
                    val adapter = MessageTabAdapter(it.messagesList) { msg ->
                        binding.tvMessage.text = msg?.messageText
                        binding.tvDate.text = msg?.messageDate
                    }
                    binding.rvMessages.adapter = adapter
                    binding.rvMessages.post {
                        binding.rvMessages.requestFocus()
                    }
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { messageViewModel.showToastMessage(getString(it)) }
            }
        }
    }
    fun handleBackClick() {
        finish()
    }
}