package com.diipl.moviebeam.ui.guestservice.news

import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.news.News
import com.diipl.moviebeam.databinding.CardNewsBinding

class NewsTabAdapter(
    private var onMenuItemFocused: (News) -> Unit,
    private val onLeftKeyPressed: () -> Unit
) : RecyclerView.Adapter<NewsTabAdapter.MyViewHolder>() {

    private var newsList: List<News> = emptyList()
    private var gradient: GradientDrawable? = null

    inner class MyViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true
        binding.root.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT ->{
                        onLeftKeyPressed()
                    }
                }
            }
            false
        }
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = newsList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = newsList[position]
        holder.binding.tvNews.text = item.title

        holder.itemView.post {
            if (position == 0){
                holder.itemView.requestFocus()
            }
        }

        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            onMenuItemFocused(item)
            if(isFocused){
                if (position == 0) {
                    view.nextFocusUpId = view.id
                }
                if (position == newsList.size.minus(1)) {
                    view.nextFocusDownId = view.id
                }
                holder.binding.clCard.background = gradient
            }else{
                if (position == 0) {
                    view.nextFocusUpId = View.NO_ID
                }
                if (position == newsList.size.minus(1)) {
                    view.nextFocusDownId = View.NO_ID
                }
                holder.binding.clCard.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
            }
        }
    }

    fun setNewsList(newsList: List<News>){
        this.newsList = newsList
    }

    fun setGradient(gradient: GradientDrawable){
        this.gradient = gradient
    }

}