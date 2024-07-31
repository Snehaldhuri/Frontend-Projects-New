package com.diipl.moviebeam.ui.movies

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent

class MoviesBtnAdapter(
    private val onMoviesMenuItemClicked: (View, contentType: String) -> Unit,
    private val onRightKeyPressed: () -> Unit
) : ListAdapter<BtnModel, MoviesBtnAdapter.MyViewHolder>(diffCallback) {

    private var selectedPosition = -1

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<BtnModel>() {
            override fun areItemsTheSame(oldItem: BtnModel, newItem: BtnModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BtnModel, newItem: BtnModel): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_menu_icon)
        val textView: TextView = itemView.findViewById(R.id.tv_menu_title)
        val card: ConstraintLayout = itemView.findViewById(R.id.clHomeMenuButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoviesBtnAdapter.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 22)
        params.height = getHeightInPercent(parent.context, 15)

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

    override fun onBindViewHolder(holder: MoviesBtnAdapter.MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.title

        if (selectedPosition == -1) {
            holder.itemView.isSelected = true
            selectedPosition = 0
            onMoviesMenuItemClicked(holder.card, item.btnId)
        }
        updateFocus(holder)

        holder.card.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                holder.card.background = getGradientColor()
                holder.card.setOnClickListener {
                    onMoviesMenuItemClicked(v, item.btnId)
                    holder.itemView.isSelected = true
                    selectedPosition = holder.absoluteAdapterPosition
                    updateFocus(holder)
                    notifyUI(holder)
                }
            } else {
//                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                updateFocus(holder)
            }
        }
    }

    private fun notifyUI(holder: MyViewHolder) {
        currentList.forEachIndexed { index, _ ->
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

}