package com.diipl.moviebeam.ui.showtime

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class ShowtimeSeasonChildAdapter(
    private val onItemClicked: (Detail) -> Unit,
    private val onLeftKeyPressed: () -> Unit
) : RecyclerView.Adapter<ShowtimeSeasonChildAdapter.ChildViewHolder>() {

    private var seasonDet: List<Detail> = emptyList()
    private var gradient: GradientDrawable? = null

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seasonImage: ImageView = itemView.findViewById(R.id.iv_season_image)
        val seasonTitle: TextView = itemView.findViewById(R.id.tv_season_name)
        val seasonDetail: TextView = itemView.findViewById(R.id.tv_season_detail)
        val movieview: CardView = itemView.findViewById(R.id.cv_season_movie_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.seasonlist_item, parent, false)
        view.isFocusable = true
        view.isClickable = true
        view.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
                }
            }
            false
        }
        return ChildViewHolder(view)
    }

    override fun getItemCount(): Int = seasonDet.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {

        val detail = seasonDet[position]
        holder.seasonImage.loadImagesWithGlideExt(detail.secImagePathPoster)
        holder.seasonTitle.text = detail.episodeHeaderDetails
        holder.seasonDetail.text = detail.synopsis

        holder.movieview.setOnClickListener {
            onItemClicked(detail)
        }

        holder.movieview.setOnFocusChangeListener { it, hasFocus ->
            val scaleX = ObjectAnimator.ofFloat(it, View.SCALE_X, 1.0f, 1.01f)
            val scaleY = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1.0f, 1.01f)

            val scaleAnimatorSet = AnimatorSet()
            scaleAnimatorSet.duration = 200
            scaleAnimatorSet.playTogether(scaleX, scaleY)

            if (hasFocus) {
                holder.movieview.background = gradient
                scaleAnimatorSet.start()
            } else {
                holder.movieview.setBackgroundResource(R.color.transparent)
                scaleAnimatorSet.cancel()
                it.scaleX = 1.0f
                it.scaleY = 1.0f
            }
        }
    }

    fun updateSeasons(newSeasons: List<Detail>) {
        this.seasonDet = newSeasons
        notifyDataSetChanged()
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient = gradient
    }
}

