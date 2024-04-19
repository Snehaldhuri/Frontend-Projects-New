package com.diipl.moviebeam.ui.guestservice.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.databinding.FragmentMessageBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class MessageFragment : BaseFragment() {

    private var _binding: FragmentMessageBinding? = null
    val binding get() = _binding!!

    private val messageViewModel: MessageViewModel by activityViewModels()

    override fun observeViewModel() {
        observe(messageViewModel.guestMessageLiveData, ::handleGuestMessageResponse)
    }

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        binding.rvMessages.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
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

}