package com.diipl.moviebeam.ui.movies

import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.databinding.MoviegenreChildlistItemBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExtSushi
import com.diipl.moviebeam.utils.toInvisible

private const val TAG = "ChildAdapter"

class ChildAdapter(
    private val childList: List<ContentDto>,
    private var onItemClicked: (ContentDto) -> Unit,
    private val onLeftKey: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(val binding: MoviegenreChildlistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val logo: ImageView = itemView.findViewById(R.id.childLogoIv)
        val title: TextView = itemView.findViewById(R.id.childTitleTv)
        val movieview: CardView = itemView.findViewById(R.id.cv_movie_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = MoviegenreChildlistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        binding.root.isFocusable = true
        binding.root.isClickable = true

        binding.root.setOnFocusChangeListener { it, hasFocus ->
            val focusedColor =
                ContextCompat.getColor(parent.context, R.color.home_page_greeting_text_color)
            val unfocusedColor = ContextCompat.getColor(parent.context, R.color.text_color_primary)

            if (hasFocus) {
                it.scaleX = 1.12f
                it.scaleY = 1.12f
                binding.childTitleTv.setTextColor(focusedColor)
                binding.imgCard.strokeColor = focusedColor
            } else {
                it.scaleX = 1.0f
                it.scaleY = 1.0f
                binding.childTitleTv.setTextColor(unfocusedColor)
                binding.imgCard.strokeColor = Color.TRANSPARENT
            }
        }


        return ChildViewHolder(binding)
    }

    override fun getItemCount(): Int = childList.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val item = childList[position]

        val httpStreamingHotelvideoUrl = "http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        item.imagePathSushi =
            httpStreamingHotelvideoUrl + item.releaseId + "/" + item.releaseId + "_S.jpg"

        holder.logo.loadImagesWithGlideExtSushi(item.imagePathSushi)
        if (item.releaseTypeId == Constants.FREE_MOVIE_RELEASE_TYPE_ID) {
            holder.title.toInvisible()
        } else {
            holder.title.text =
                holder.movieview.context.getString(R.string.price_dollar, item.price.toString())
        }

        if (item.releaseTypeId == Constants.FREE_MOVIE_RELEASE_TYPE_ID) {
            holder.title.toInvisible()
        } else {
            holder.title.text =
                holder.movieview.context.getString(R.string.price_dollar, item.price.toString())
        }

        holder.itemView.setOnKeyListener { v, keycode, keyEvent ->
            when (keycode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (holder.absoluteAdapterPosition == 1) {
                        onLeftKey(true)
                        Log.e(TAG, "KEYCODE_DPAD_LEFT ${holder.absoluteAdapterPosition} ")
                    }
                }
            }
            false
        }

        holder.movieview.setOnClickListener {
            onItemClicked(item)
        }
    }

}