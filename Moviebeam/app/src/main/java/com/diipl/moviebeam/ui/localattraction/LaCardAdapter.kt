package com.diipl.moviebeam.ui.localattraction

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
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class LaCardAdapter(
    private var onMenuItemClicked: (String) -> Unit
) :
    RecyclerView.Adapter<LaCardAdapter.MyViewHolder>() {
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
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.la_carousel, parent, false)

        view.isClickable = true

        view.findViewById<CardView>(R.id.front_card).background.setTint(
            Color.parseColor(
                defaultColor
            )
        )
        view.setOnFocusChangeListener { it, hasFocus ->
            if (hasFocus) {
                it.findViewById<CardView>(R.id.front_card).background = gradientDrawable

                val scaleX = ObjectAnimator.ofFloat(it, View.SCALE_X, 1.0f, 1.02f)
                val scaleY = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1.0f, 1.02f)

                val scaleAnimatorSet = AnimatorSet()
                scaleAnimatorSet.duration = 200
                scaleAnimatorSet.playTogether(scaleX, scaleY)
                scaleAnimatorSet.start()
            } else {
//                it.findViewById<CardView>(R.id.front_card).background = null
                it.scaleX = 1.0f
                it.scaleY = 1.0f
            }
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.imageView.loadImagesWithGlideExt(item.imagePathPoster)
        holder.textView.text = item.title
        holder.description.text = item.description.replace("<br/>", "")

        holder.frontCard.visibility = View.VISIBLE
    }

    fun setGradientDrawable(gradient: GradientDrawable) {
        gradientDrawable = gradient
    }

    fun setList(itemList: List<LAService>) {
        this.itemList = itemList
    }
}


