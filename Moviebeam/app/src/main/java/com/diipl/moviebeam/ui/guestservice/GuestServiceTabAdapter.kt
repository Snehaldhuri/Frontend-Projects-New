package com.diipl.moviebeam.ui.guestservice

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.GsBtnModel

class GuestServiceTabAdapter(
    private var onMenuItemClicked: (View, GsBtnModel) -> Unit
) : RecyclerView.Adapter<GuestServiceTabAdapter.MyViewHolder>() {

    private var startColor = ""
    private var endColor = ""

    private var itemList: List<GsBtnModel> = mutableListOf()

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
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

//        holder.imageView.setImageResource(item.defaultImage)
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
                holder.card.setOnClickListener {
//                    Log.d("TAG CLICK", "onBindViewHolder: ${Constants.GUEST_SERVICE_BUTTON_LIST[2].categoryName}-${Constants.GUEST_SERVICE_BUTTON_LIST[2].isClicked}")
                    itemList.forEach { btn ->
                        if (btn == item) {
                            btn.isClicked = true
                            onMenuItemClicked(it, item)
                        } else {
                            if(btn.isClicked) notifyItemChanged(itemList.indexOf(btn))
                            btn.isClicked = false
                            holder.imageView.setImageResource(item.spotlightImage)
                            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_spotlight)
                        }
                    }
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


//            if (btn.isClicked) {
//
//            } else {
//                btn.isClicked = false
//            }

//        holder.card.setOnClickListener {
//            item.isClicked = true
//
//
//            onMenuItemClicked(it, item)
////            it.setBackgroundColor(Color.parseColor("#EBEBEB"))
//        }
    }

    private fun setFocus(cardView: ConstraintLayout) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        cardView.background = gradientDrawable
    }

    fun setButtonList(btnList: List<GsBtnModel>) {
        itemList = btnList
    }

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }

}