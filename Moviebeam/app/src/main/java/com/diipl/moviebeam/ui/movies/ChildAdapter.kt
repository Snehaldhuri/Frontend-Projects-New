package com.diipl.moviebeam.ui.movies

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.databinding.MoviegenreChildlistItemBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.Constants.MOVIE_SELECTED_POSITION
import com.diipl.moviebeam.utils.loadImagesWithGlideExtSushi
import com.diipl.moviebeam.utils.toInvisible

private const val TAG = "ChildAdapter"

class ChildAdapter(
    private val childList: List<ContentDto>,
    private var onItemClicked: (ContentDto, View) -> Unit,
    private val onLeftKey: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {
    private var pos = -1
    private var viewHolder : ChildViewHolder? = null

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

            var anim: Animation = AnimationUtils.loadAnimation(parent.context, R.anim.scale_out_animation)
            binding.childTitleTv.setTextColor(unfocusedColor)
            binding.imgCard.strokeColor = Color.TRANSPARENT

            if (hasFocus) {
                anim = AnimationUtils.loadAnimation(parent.context, R.anim.scale_in_animation)
                binding.childTitleTv.setTextColor(focusedColor)
                binding.imgCard.strokeColor = focusedColor
            }
            binding.root.startAnimation(anim)
            anim.fillAfter = true

        }

        return ChildViewHolder(binding)
    }

    override fun getItemCount(): Int = childList.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        viewHolder = holder
        val item = childList[position]

        val httpStreamingHotelVideoUrl = "http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        val url = httpStreamingHotelVideoUrl + item.releaseId + "/" + item.releaseId + "_S.jpg"

        holder.logo.loadImagesWithGlideExtSushi(url)
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

        holder.itemView.setOnKeyListener { _, keycode, _ ->
            when (keycode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (holder.absoluteAdapterPosition == 0 && pos == -1){
                        onLeftKey(true)
                    }
                    pos = if (holder.absoluteAdapterPosition == 0 && pos != 0){
                        -1
                    } else {
                        holder.absoluteAdapterPosition
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        pos = if (holder.absoluteAdapterPosition == 0 && pos != 0){
                            -1
                        }
                        else {
                            holder.absoluteAdapterPosition
                        }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    pos = if (holder.absoluteAdapterPosition == 0 && pos != 0){
                        -1
                    } else {
                        holder.absoluteAdapterPosition
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    pos = if (holder.absoluteAdapterPosition == 0 && pos != 0){
                        -1
                    } else {
                        holder.absoluteAdapterPosition
                    }
                }
            }
            false
        }

        holder.movieview.setOnClickListener {
            MOVIE_SELECTED_POSITION = holder.absoluteAdapterPosition
            onItemClicked(item, holder.itemView)
        }
    }


}