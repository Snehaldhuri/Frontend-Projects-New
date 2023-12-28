package com.diipl.moviebeam.ui.movies

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class ChildAdapter(
    private val childList: List<ContentDto>,
    private var onItemClicked: (ContentDto) -> Unit
) :
    RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.findViewById(R.id.childLogoIv)
        val title: TextView = itemView.findViewById(R.id.childTitleTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.child_item, parent, false)
        view.isFocusable = true
        view.isClickable = true
        view.setOnFocusChangeListener { it, hasFocus ->
        val item = childList[position]
        holder.title.setText("$ " + childList[position].price.toString())
            val scaleX = ObjectAnimator.ofFloat(it, View.SCALE_X, 1.0f, 1.1f)
            val scaleY = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1.0f, 1.1f)

            val scaleAnimatorSet = AnimatorSet()
            scaleAnimatorSet.duration = 200
            scaleAnimatorSet.playTogether(scaleX, scaleY)

            if (hasFocus) {
                scaleAnimatorSet.start()
            } else {
                scaleAnimatorSet?.cancel()
                it.scaleX = 1.0f
                it.scaleY = 1.0f
            }
        }
        holder.movieview.setOnClickListener {
            onItemClicked(item)
        }

        return ChildViewHolder(view)
    }

    override fun getItemCount(): Int {
        return childList.size
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.logo.loadImagesWithGlideExt(childList[position].secImagePathSushi)
        holder.title.setText("$ "+ childList[position].price.toString())

    }

}