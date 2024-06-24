package com.diipl.moviebeam.ui.guestservice.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.message.MessageResponse
import com.diipl.moviebeam.databinding.CardNewsBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor

class MessageTabAdapter(
    private val messageList: List<MessageResponse>?,
    private var onMessageFocused: (MessageResponse?) -> Unit
) : RecyclerView.Adapter<MessageTabAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = messageList?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = messageList?.get(position)
        holder.binding.tvNews.text = item?.messageSubject
        holder.binding.root.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                onMessageFocused(item)
                holder.binding.clCard.background = getGradientColor()
            } else {
                holder.binding.clCard.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
    }

}