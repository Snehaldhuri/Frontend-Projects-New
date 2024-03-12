package com.diipl.moviebeam.ui.hotelinfo

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent

class HotelInfoTabAdapter(
    private val itemList: List<String>,
    private var onItemFocused: ((String),View) -> Unit,
    private var onHelpInfoTabClick: ((String), (Int),View) -> Unit,
    private val onFocusChangeListener: OnFocusChangeListener
) :
    RecyclerView.Adapter<HotelInfoTabAdapter.MyViewHolder>() {

    private var startColor = "#85bf08"
    private var endColor = "#0ca654"

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_tabInfo)
        val card: ConstraintLayout = itemView.findViewById(R.id.card1)
    }
    interface OnFocusChangeListener {
        fun onItemFocused(position: Int, itemList: List<String>)
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
        holder.setIsRecyclable(false)
        val item = itemList[position]

        holder.textView.text = item
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        holder.card.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                onItemFocused(itemList[position], view)
                onFocusChangeListener.onItemFocused(position, itemList)
                fetchGradientColorsFromApi(holder.card)
            } else {
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        if (holder.textView.text == Constants.HELP_INFO) {
            holder.card.setOnClickListener {
                onItemFocused(itemList[position],it)
                onHelpInfoTabClick(itemList[position], position,it)
            }
        }
    }

    private fun fetchGradientColorsFromApi(cardView: ConstraintLayout) {
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

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }

}
