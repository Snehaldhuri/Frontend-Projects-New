package com.diipl.moviebeam.ui.guestservice.news

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.news.NewsHeader
import com.diipl.moviebeam.databinding.CardNewsBinding

class NewsHeaderTabAdapter(
    private var onMenuItemClicked: (NewsHeader) -> Unit
) : RecyclerView.Adapter<NewsHeaderTabAdapter.MyViewHolder>() {

    private var newsHeaderList: List<NewsHeader> = emptyList()
    private var gradient: GradientDrawable? = null

    inner class MyViewHolder(val binding: CardNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = newsHeaderList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = newsHeaderList[position]
        holder.binding.tvNews.text = item.headerName
        holder.binding.root.setOnClickListener {
            onMenuItemClicked(item)
        }
        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            if(isFocused){
                holder.binding.clCard.background = gradient
            }else{
                holder.binding.clCard.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
            }
        }
    }

    fun setNewsHeaderList(newsHeaderList: List<NewsHeader>) {
        this.newsHeaderList = newsHeaderList
    }

    fun setGradient(gradient: GradientDrawable){
        this.gradient = gradient
    }

}