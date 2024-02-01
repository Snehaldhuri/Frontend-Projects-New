package com.diipl.moviebeam.ui.guestservice.localAttraction

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLA

class LaCardAdapterGs(
    private var onMenuItemClicked: (String) -> Unit
) : RecyclerView.Adapter<LaCardAdapterGs.MyViewHolder>() {
    private val defaultColor = "#FFFFFF"
    private var gradientDrawable: GradientDrawable? = null

    private var itemList: List<LAService> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_la_card_image)
        val textView: TextView = itemView.findViewById(R.id.tv_la_card_title)
        val description: TextView = itemView.findViewById(R.id.tv_la_card_desc)
        val frontCard: CardView = itemView.findViewById(R.id.front_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.la_carousel, parent, false)
        view.isClickable = true

        view.findViewById<CardView>(R.id.front_card).background.setTint(
            Color.parseColor(
                defaultColor
            )
        )

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.imageView.loadImagesWithGlideExtLA(item.imagePathPoster)
        holder.textView.text = item.title
        holder.description.text = item.description.replace("<br/>", "")

        holder.frontCard.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.background = gradientDrawable

                val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 1.02f)
                val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 1.02f)

                val scaleAnimatorSet = AnimatorSet()
                scaleAnimatorSet.duration = 200
                scaleAnimatorSet.playTogether(scaleX, scaleY)
                scaleAnimatorSet.start()
            } else {
                view.scaleX = 1.0f
                view.scaleY = 1.0f
            }

            if (position == 0) {
                if (hasFocus) {
                    view.nextFocusUpId = view.id
                } else {
                    view.nextFocusUpId = View.NO_ID
                }
            } else if (position == itemList.size - 1) {
                if (hasFocus) {
                    view.nextFocusDownId = view.id
                } else {
                    view.nextFocusDownId = View.NO_ID
                }
            }

        }

        holder.frontCard.visibility = View.VISIBLE
    }

    fun setGradientDrawable(gradient: GradientDrawable) {
        gradientDrawable = gradient
    }

    fun setList(itemList: List<LAService>) {
        this.itemList = itemList
    }
}


