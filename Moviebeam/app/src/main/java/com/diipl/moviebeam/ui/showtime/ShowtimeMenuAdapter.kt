package com.diipl.moviebeam.ui.showtime

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel

class ShowtimeMenuAdapter(
    private val itemList: List<BtnModel>,
    private val onMoviesMenuItemClicked: (View, contentType: String) -> Unit,
    private val onRightKeyPressed: () -> Unit
) : RecyclerView.Adapter<ShowtimeMenuAdapter.MyViewHolder>() {

    private var gradient: GradientDrawable? = null
    private var selectedPosition = -1

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_menu_icon)
        val textView: TextView = itemView.findViewById(R.id.tv_menu_title)
        val card: ConstraintLayout = itemView.findViewById(R.id.clHomeMenuButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShowtimeMenuAdapter.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        val layoutParams = ViewGroup.MarginLayoutParams(view.layoutParams)
        layoutParams.setMargins(0, 0, 0, 3)
        view.layoutParams = layoutParams

        view.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        onRightKeyPressed()
                    }
                }
            }
            false
        }
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowtimeMenuAdapter.MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.title

        if (selectedPosition == -1) {
            holder.itemView.isSelected = true
            selectedPosition = 0
            onMoviesMenuItemClicked(holder.card, item.btnId)
        }
        updateFocus(holder)

//        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

        holder.card.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.background = gradient
            } else {
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        holder.card.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.background = gradient
                holder.card.setOnClickListener {
                    onMoviesMenuItemClicked(view, item.btnId)
                    holder.itemView.isSelected = true
                    selectedPosition = holder.absoluteAdapterPosition
                    updateFocus(holder)
                    notifyUI()
                }
            } else {
//                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                updateFocus(holder)
            }
        }
    }

    private fun notifyUI() {
        itemList.forEachIndexed { index, _ ->
            if (selectedPosition != index)
                notifyItemChanged(index)
        }
    }

    private fun updateFocus(holder: MyViewHolder) {
        if (selectedPosition == holder.absoluteAdapterPosition && holder.itemView.isSelected) {
            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_spotlight)
            holder.imageView.imageTintList = ColorStateList.valueOf(Color.BLACK)
        } else {
            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            holder.imageView.imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun setGradient(gradient: GradientDrawable?) {
        this.gradient = gradient
    }

}