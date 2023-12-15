package com.diipl.moviebeam.ui.hotelinfo

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R

class HotelInfoTabAdapter(
    private val itemList: List<String>,
    private var onItemFocused: ((String)) -> Unit
) :
    RecyclerView.Adapter<HotelInfoTabAdapter.MyViewHolder>() {
    private var gradientDrawable: GradientDrawable? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_tabInfo)
        val card: ConstraintLayout = itemView.findViewById(R.id.card1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hotel_info_tab, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.textView.text = item
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        holder.card.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onItemFocused(itemList[position])
                holder.card.background = gradientDrawable
            } else {
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        if (holder.textView.text == "Help & Info") {
            holder.card.setOnClickListener {
                holder.card.context.startActivity(
                    Intent(
                        holder.card.context,
                        HelpInfoActivity::class.java
                    )
                )
            }
        }
    }

    fun setGradientDrawable(gradient: GradientDrawable) {
        gradientDrawable = gradient
    }

}
