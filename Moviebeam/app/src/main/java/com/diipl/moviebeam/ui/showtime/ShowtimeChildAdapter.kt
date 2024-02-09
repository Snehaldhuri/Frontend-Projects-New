package com.diipl.moviebeam.ui.showtime

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.databinding.MoviegenreChildlistItemBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExtSushi

class ShowtimeChildAdapter(
    private val childList: List<Detail>,
    private var onItemClicked: (Detail, Int) -> Unit,
    private val onLeftKey: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ShowtimeChildAdapter.ChildViewHolder>() {

    private var pos = "-1"

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
            val focusedColor = ContextCompat.getColor(parent.context, R.color.home_page_greeting_text_color)
            val unfocusedColor = ContextCompat.getColor(parent.context, R.color.text_color_primary)

            var anim: Animation = AnimationUtils.loadAnimation(parent.context, R.anim.scale_out_animation)
            binding.imgCard.strokeColor = Color.TRANSPARENT

            if (hasFocus) {
                anim = AnimationUtils.loadAnimation(parent.context, R.anim.scale_in_animation)
                binding.imgCard.strokeColor = focusedColor
            }
            binding.root.startAnimation(anim)
            anim.fillAfter = true

           /* if (hasFocus) {
                it.scaleX = 1.12f
                it.scaleY = 1.12f
                binding.childTitleTv.setTextColor(focusedColor)
                binding.imgCard.strokeColor = focusedColor
//                binding.childTitleTv.toVisible()
            } else {
                it.scaleX = 1.0f
                it.scaleY = 1.0f
                binding.childTitleTv.setTextColor(unfocusedColor)
                binding.imgCard.strokeColor = Color.TRANSPARENT
//                binding.childTitleTv.toInvisible()
            }*/
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
//        holder.binding.childTitleTv.text = item.movieName
//        holder.binding.childTitleTv.toInvisible()
        holder.movieview.setOnClickListener {
            onItemClicked(item, item.releaseId)
        }

        holder.itemView.setOnKeyListener { v, keycode, _ ->
            when (keycode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (holder.absoluteAdapterPosition == 0 && pos == "-1"){
                        onLeftKey(true)
                    }
                    pos = if (holder.absoluteAdapterPosition == 0 && pos != "0"){
                        "-1"
                    } else {
                        holder.absoluteAdapterPosition.toString()
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    pos = holder.absoluteAdapterPosition.toString()
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    pos = if (holder.absoluteAdapterPosition == 0 && pos != "0"){
                        "-1"
                    } else {
                        holder.absoluteAdapterPosition.toString()
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    pos = if (holder.absoluteAdapterPosition == 0 && pos != "0"){
                        "-1"
                    } else {
                        holder.absoluteAdapterPosition.toString()
                    }
                }
            }
            false
        }


    }
}