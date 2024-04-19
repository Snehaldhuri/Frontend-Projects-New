package com.diipl.moviebeam.ui.guestservice.localAttraction

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.text.Html
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
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLA

private const val TAG = "LaCardAdapterGs"

class LaCardAdapterGs(
    private var onLeftKeyClicked: (View) -> Unit
) : RecyclerView.Adapter<LaCardAdapterGs.MyViewHolder>() {

    private var itemList: List<LAService> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_la_card_image)
        val textView: TextView = itemView.findViewById(R.id.tv_la_card_title)
        val description: TextView = itemView.findViewById(R.id.tv_la_card_desc)
        val frontCard: CardView = itemView.findViewById(R.id.front_card)

        val backCard: CardView = itemView.findViewById(R.id.back_card)
        val flipButton: Button = itemView.findViewById(R.id.btn_MoreInfo)
        val pressOkText = itemView.findViewById<TextView>(R.id.text3)

        val scanImage: ImageView = itemView.findViewById(R.id.scan_qr_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.la_carousel, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 40)
        params.height = getHeightInPercent(parent.context, 67)
        view.handleFocusChange()
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: LaCardAdapterGs.MyViewHolder, position: Int) {

        val item = itemList[position]
        holder.imageView.loadImagesWithGlideExtLA(item.imagePathPoster)
        holder.textView.text = item.title
        holder.description.text = item.description.replace("<br/>", "")

        val text = "Press <font color='#FFB81A'>OK</font> to go back"
        holder.pressOkText.text = Html.fromHtml(text)

        holder.flipButton.setOnClickListener {
            flipImage(holder.frontCard, holder.backCard)
            holder.itemView.requestFocus()
        }

        holder.scanImage.setOnClickListener {
            unFlipImage(holder.frontCard, holder.backCard)
            holder.itemView.requestFocus()
        }

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (position == 0) {
                if (hasFocus) {
                    view.nextFocusUpId = view.id
                } else {
                    view.nextFocusUpId = View.NO_ID
                }
            } else if (position == itemList.size.minus(1)) {
                if (hasFocus) {
                    view.nextFocusDownId = view.id
                } else {
                    view.nextFocusDownId = View.NO_ID
                }
            }

            if (hasFocus) {

                holder.frontCard.isClickable = false
                holder.flipButton.background = getGradientColor()

                val scaleX = ObjectAnimator.ofFloat(holder.itemView, View.SCALE_X, 1.0f, 1.02f)
                val scaleY = ObjectAnimator.ofFloat(holder.itemView, View.SCALE_Y, 1.0f, 1.02f)

                val scaleAnimatorSet = AnimatorSet()
                scaleAnimatorSet.duration = 200
                scaleAnimatorSet.playTogether(scaleX, scaleY)
                scaleAnimatorSet.start()

                view.setOnKeyListener { _, code, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (code) {
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                if (holder.frontCard.visibility == View.VISIBLE) {
                                    flipImage(holder.frontCard, holder.backCard)
//                                    holder.okButton.background = gradientDrawable
//                                    holder.backCard.requestFocus()
                                }
                                if (holder.backCard.visibility == View.VISIBLE) {
                                    holder.scanImage.post {
                                        holder.scanImage.requestFocus()
                                    }
                                    holder.scanImage.setOnFocusChangeListener { b, focus ->
                                        if (focus) {
                                            b.setOnKeyListener { _, keyCode, event ->
                                                if (event.action == KeyEvent.ACTION_DOWN) {
                                                    when (keyCode) {
                                                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                                            unFlipImage(
                                                                holder.frontCard,
                                                                holder.backCard
                                                            )
                                                            holder.flipButton.requestFocus()
                                                        }

                                                    }
                                                }
                                                false
                                            }
                                        }
                                    }
                                    return@setOnKeyListener true
                                }
                            }
                        }
                    }
                    when (code) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onLeftKeyClicked(view)
                        }
                    }
                    false
                }


            } else {
                holder.itemView.scaleX = 1.0f
                holder.itemView.scaleY = 1.0f
                if (holder.frontCard.visibility == View.VISIBLE) {
                    holder.flipButton.background = getGradientColor()
                    holder.flipButton.requestFocus()
                } else {
                    holder.scanImage.requestFocus()
                }
            }
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
    }

    fun setList(itemList: List<LAService>) {
        this.itemList = itemList
    }
}


