package com.diipl.moviebeam.ui.showtime

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class ShowtimeSeasonChildAdapter(
    private val onItemClicked: (ShowTimeContent) -> Unit
) : RecyclerView.Adapter<ShowtimeSeasonChildAdapter.ChildViewHolder>() {

    private var seasonDet: List<Detail> = emptyList()

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

        view.setOnFocusChangeListener { it, hasFocus ->
            val scaleX = ObjectAnimator.ofFloat(it, View.SCALE_X, 1.0f, 1.1f)
            val scaleY = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1.0f, 1.1f)

            val scaleAnimatorSet = AnimatorSet()
            scaleAnimatorSet.duration = 200
            scaleAnimatorSet.playTogether(scaleX, scaleY)

            if (hasFocus) {
                scaleAnimatorSet.start()
            } else {
                scaleAnimatorSet.cancel()
                it.scaleX = 1.0f
                it.scaleY = 1.0f
            }
        }
        return ChildViewHolder(view)
    }

    override fun getItemCount(): Int = seasonDet.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {

        val detail = seasonDet.get(position)
        if (detail != null) {
            holder.seasonImage.loadImagesWithGlideExt(detail.secImagePathSushi)
        }
        holder.seasonTitle.text = detail?.episodeHeaderDetails
        holder.seasonDetail.text = detail?.synopsis

//        holder.movieview.setOnClickListener {
//            onItemClicked(detail)
//        }
    }

    fun updateSeasons(newSeasons: List<Detail>) {
        this.seasonDet = newSeasons
        notifyDataSetChanged()
    }

}

