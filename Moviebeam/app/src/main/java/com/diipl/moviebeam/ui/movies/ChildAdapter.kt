package com.diipl.moviebeam.ui.movies

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.toInvisible

class ChildAdapter(
    private val childList: List<ContentDto>,
    private var onItemClicked: (ContentDto) -> Unit
) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

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
        holder.logo.loadImagesWithGlideExt(item.secImagePathSushi)
        if(item.releaseTypeId == Constants.FREE_MOVIE_RELEASE_TYPE_ID){
            holder.title.toInvisible()
        }else{
            holder.title.text =
                holder.movieview.context.getString(R.string.price_dollar, item.price.toString())
        }

        holder.movieview.setOnClickListener {
            onItemClicked(item)
        }
    }

}