package com.diipl.moviebeam.ui.showtime

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel

class ShowtimeMenuAdapter(
    private val itemList: List<BtnModel>,
    private val onMoviesMenuItemClicked: (contentType: String) -> Unit
) :
    RecyclerView.Adapter<ShowtimeMenuAdapter.MyViewHolder>() {
    var startColor = ""
    var endColor = ""

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_menu_icon)
        val textView: TextView = itemView.findViewById(R.id.tv_menu_title)
        val card: ConstraintLayout = itemView.findViewById(R.id.clHomeMenuButton)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ShowtimeMenuAdapter.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        val layoutParams = ViewGroup.MarginLayoutParams(view.layoutParams)
        layoutParams.setMargins(0, 0, 0, 3)
        view.layoutParams = layoutParams
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowtimeMenuAdapter.MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.title

        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

        holder.card.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                fetchGradientColorsFromApi(holder.card)
            } else {
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        holder.card.setOnClickListener {
            onMoviesMenuItemClicked(item.btnId)

        }
    }
    override fun getItemCount(): Int =  itemList.size

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