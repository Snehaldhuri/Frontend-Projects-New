package com.diipl.moviebeam.ui.hotelinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.handleFocusChange

class HelpInfoTabAdapter(
    private val itemList: List<String>
) :
    RecyclerView.Adapter<HelpInfoTabAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_tabInfo)
        val card: ConstraintLayout = itemView.findViewById(R.id.card1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hotel_info_tab, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 22)
        params.height = getHeightInPercent(parent.context, 15)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.textView.text = item
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        holder.card.handleFocusChange()
    }


}