package com.diipl.moviebeam.ui.guestservice

import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.GsBtnModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent

class GuestServiceTabAdapter(
    private var onMenuItemClicked: (View, GsBtnModel) -> Unit,
    private var onRightClicked: (View) -> Unit,
    private val onFocusChangeListener: OnFocusChangeListener

) : RecyclerView.Adapter<GuestServiceTabAdapter.MyViewHolder>() {

    var itemList: List<GsBtnModel> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_menu_icon)
        val textView: TextView = itemView.findViewById(R.id.tv_menu_title)
        val card: ConstraintLayout = itemView.findViewById(R.id.clHomeMenuButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 22)
        params.height = getHeightInPercent(parent.context, 15)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size
    interface OnFocusChangeListener {
        fun onItemFocused(position: Int, itemList: List<GsBtnModel>)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.textView.text = item.categoryName

        if (item.isClicked) {
            holder.imageView.setImageResource(item.spotlightImage)
            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_spotlight)

        } else {
            holder.imageView.setImageResource(item.defaultImage)
            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
        holder.card.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setFocus(holder.card)
                onFocusChangeListener.onItemFocused(position, itemList)

                holder.card.setOnClickListener {
//                    Log.d("TAG CLICK", "onBindViewHolder: ${Constants.GUEST_SERVICE_BUTTON_LIST[2].categoryName}-${Constants.GUEST_SERVICE_BUTTON_LIST[2].isClicked}")
                    itemList.forEach { btn ->
                        if (btn == item) {
                            btn.isClicked = true
                            onMenuItemClicked(it, item)
                        } else {
                            if (btn.isClicked)
                                notifyItemChanged(itemList.indexOf(btn))
                            holder.imageView.setImageResource(item.spotlightImage)
                            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_spotlight)

                            btn.isClicked = false


                        }
                    }
                    setFocus(holder.card)

                }

                holder.card.setOnKeyListener { view, code, keyEvent ->
                    when (code) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            onRightClicked(view)
                        }
                    }
                    false
                }

            } else {
                if (item.isClicked) {
                    holder.imageView.setImageResource(item.spotlightImage)
                    holder.textView.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_spotlight)
                } else {
                    holder.imageView.setImageResource(item.defaultImage)
                    holder.textView.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                }
            }
        }

        holder.itemView.post {
            if (holder.absoluteAdapterPosition == 0) {
                holder.card.requestFocus()
//                item.isClicked = true
//                onMenuItemClicked(holder.card, item)
            }
        }
    }

    private fun setFocus(cardView: ConstraintLayout) {
        cardView.background = getGradientColor()
    }

    fun setButtonList(btnList: List<GsBtnModel>) {
        itemList = btnList
    }

}