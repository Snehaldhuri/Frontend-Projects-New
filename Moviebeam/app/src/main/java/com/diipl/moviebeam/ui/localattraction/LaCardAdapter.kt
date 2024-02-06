package com.diipl.moviebeam.ui.localattraction

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.ui.guestservice.localAttraction.LaCardAdapterGs
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLA

class LaCardAdapter(
    private var onMenuItemClicked: (String) -> Unit
) :
    RecyclerView.Adapter<LaCardAdapter.MyViewHolder>() {
    private val defaultColor = "#FFFFFF"
    private var gradientDrawable: GradientDrawable? = null

    private var itemList: List<LAService> = mutableListOf()
    var isFront = true
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_la_card_image)
        val textView: TextView = itemView.findViewById(R.id.tv_la_card_title)
        val description: TextView = itemView.findViewById(R.id.tv_la_card_desc)
        val frontCard: CardView = itemView.findViewById(R.id.front_card)

        val backCard: CardView = itemView.findViewById(R.id.back_card)
        val flipButton: Button = itemView.findViewById(R.id.btn_MoreInfo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.la_carousel, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = itemList[position]
        holder.imageView.loadImagesWithGlideExtLA(item.imagePathPoster)
        holder.textView.text = item.title
        holder.description.text = item.description.replace("<br/>", "")

        holder.flipButton.setOnClickListener {
            Log.e("onBindViewHolder:", "flipButton")
            flipImage(holder.frontCard, holder.backCard)
        }

        holder.flipButton.requestFocus()
        holder.flipButton.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.frontCard.isClickable = false
                holder.flipButton.background = gradientDrawable

                val scaleX = ObjectAnimator.ofFloat(holder.itemView, View.SCALE_X, 1.0f, 1.02f)
                val scaleY = ObjectAnimator.ofFloat(holder.itemView, View.SCALE_Y, 1.0f, 1.02f)

                val scaleAnimatorSet = AnimatorSet()
                scaleAnimatorSet.duration = 200
                scaleAnimatorSet.playTogether(scaleX, scaleY)
                scaleAnimatorSet.start()
            } else {
                holder.itemView.scaleX = 1.0f
                holder.itemView.scaleY = 1.0f
            }
        }
        holder.flipButton.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER -> {
                        if (isFront) {
                            flipImage(holder.frontCard, holder.backCard)
                        } else {
                            unFlipImage(holder.frontCard, holder.backCard)
                        }

                    }
                }
            }
             false
        }


    }
    private fun flipImage(back: CardView, front: CardView) {
        val flipAnimator = ObjectAnimator.ofFloat(back, "rotationY", 0f, 360f)
        flipAnimator.duration = 500
        flipAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                back.visibility = View.GONE
                front.visibility = View.VISIBLE
            }
        })
        flipAnimator.start()
        isFront=false
    }

    private fun unFlipImage(back: CardView, front: CardView) {
        val unFlipAnimator = ObjectAnimator.ofFloat(front, "rotationY", 360f, 0f)
        unFlipAnimator.duration = 500
        unFlipAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                front.visibility = View.GONE
                back.visibility = View.VISIBLE
            }
        })
        unFlipAnimator.start()
        isFront=true
    }

    fun setGradientDrawable(gradient: GradientDrawable) {
        gradientDrawable = gradient
    }

    fun setList(itemList: List<LAService>) {
        this.itemList = itemList
    }}


