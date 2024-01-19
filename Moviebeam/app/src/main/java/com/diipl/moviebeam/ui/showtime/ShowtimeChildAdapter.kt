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
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.utils.loadImagesWithGlideExtSushi
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeGenre

class ShowtimeChildAdapter(
    private val childList: List<Detail>,
    private var onItemClicked: (Detail,Int) -> Unit
) :
    RecyclerView.Adapter<ShowtimeChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.childLogoIv)
        val title: TextView = itemView.findViewById(R.id.childTitleTv)
        val movieview: CardView = itemView.findViewById(R.id.cv_movie_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moviegenre_childlist_item, parent, false)
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

    override fun getItemCount(): Int = childList.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val item = childList[position]

        val httpStreamingHotelvideoUrl ="http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
        item.imagePathSushi = httpStreamingHotelvideoUrl+item.releaseId+"/"+item.releaseId+"_S.jpg"

        holder.logo.loadImagesWithGlideExtSushi(item.imagePathSushi)
        holder.movieview.setOnClickListener {
            onItemClicked(item,item.releaseId)
        }
    }
}