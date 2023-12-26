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
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class MoviesCardAdapter(private val onMoviesMenuItemClicked: (Int) -> Unit) :
    RecyclerView.Adapter<MoviesCardAdapter.MyViewHolder>() {

    private val itemList: MutableList<Any> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_movie_image)
        val textView: TextView = itemView.findViewById(R.id.tv_movie_name)
        val frontCard: CardView = itemView.findViewById(R.id.cv_movie_card)

        init {

            frontCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    if (item is ContentDto) {
                        onMoviesMenuItemClicked(item.id)
                    } else if (item is ContentDto) {
                        onMoviesMenuItemClicked(item.id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movies_card, parent, false)
        view.setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus) {

                val scaleX = ObjectAnimator.ofFloat(it, View.SCALE_X, 1.0f, 1.04f)
                val scaleY = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1.0f, 1.04f)

                val scaleAnimatorSet = AnimatorSet()
                scaleAnimatorSet.duration = 200
                scaleAnimatorSet.playTogether(scaleX, scaleY)
                scaleAnimatorSet.start()
            } else {
                it.scaleX = 1.0f
                it.scaleY = 1.0f
            }
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        if (item is ContentDto) {
            holder.imageView.loadImagesWithGlideExt(item.secImagePathPoster)
            holder.textView.setText("$ "+item.price.toString())
        } else if (item is ContentDto) {
            holder.imageView.loadImagesWithGlideExt(item.secImagePathPoster)
        }
    }
    fun setContentList(list: List<Any>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }
}
