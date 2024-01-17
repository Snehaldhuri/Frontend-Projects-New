package com.diipl.moviebeam.ui.guestservice.localAttraction

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.localattraction.LAServices

class LocalAttractionGsAdapter(private var onItemClicked: ((LAServices)) -> Unit) :

    RecyclerView.Adapter<LocalAttractionGsAdapter.MyViewHolder>() {
    private var gradientDrawable: GradientDrawable? = null

    private var itemList = listOf<LAServices>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_tabInfo)
        val card: ConstraintLayout = itemView.findViewById(R.id.card1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hotel_info_tab, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 23)
        params.height = getHeightInPercent(parent.context, 13)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int =
        itemList.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.textView.text = item.categoryName
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

        holder.card.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.card.background = gradientDrawable
                onItemClicked(item)
            } else {
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
//        holder.card.setOnClickListener {
//            onItemClicked(item)
//        }

    }

    fun setGradientDrawable(gradient: GradientDrawable) {
        gradientDrawable = gradient
    }

    fun setItemList(btnList: List<LAServices>) {
        itemList = btnList
    }

    private fun getWidthInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.widthPixels ?: 0
        return (width * percent) / 100
    }

    private fun getHeightInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.heightPixels ?: 0
        return (width * percent) / 100
    }

}