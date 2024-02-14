package com.diipl.moviebeam.ui.guestservice.concierge

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.ConciergeBtnModel

class ConciergeAdapter(
    private val onMenuItemClicked: (View, Int, ConciergeBtnModel) -> Unit,
) : RecyclerView.Adapter<ConciergeAdapter.MyViewHolder>() {

    private var startColor = ""
    private var endColor = ""

    private var conPosition = 0

    private var itemList: List<ConciergeBtnModel> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_card_image)
        val textView: TextView = itemView.findViewById(R.id.tv_card_content)
        val card: CardView = itemView.findViewById(R.id.card)
        val container: ConstraintLayout = itemView.findViewById(R.id.card_container)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.concierge_card, parent, false)
        view.isFocusable = true
        val params = view.layoutParams
        params.height = getHeightInPercent(parent.context, 26)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.imageView.setImageResource(item.defaultImage)
        holder.textView.text = item.categoryName
        holder.card.postDelayed({
            if (position == conPosition) {
                holder.card.requestFocus()
            }
        }, 1)

        holder.card.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setFocus(holder.container)
            } else {
                holder.container.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        holder.card.setOnClickListener {
            onMenuItemClicked(it, position, item)
        }
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

    private fun getHeightInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.heightPixels ?: 0
        return (width * percent) / 100
    }

    fun setButtonList(btnList: List<ConciergeBtnModel>) {
        itemList = btnList
    }

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }

    fun getFocus(conPosition: Int) {
        this.conPosition = conPosition
    }
}