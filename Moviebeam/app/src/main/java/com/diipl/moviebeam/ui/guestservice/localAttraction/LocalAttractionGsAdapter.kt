package com.diipl.moviebeam.ui.guestservice.localAttraction

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.localattraction.LAServices
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent

private const val TAG = "LAGsAdapter"

class LocalAttractionGsAdapter(
    private var onItemClicked: (View, (LAServices)) -> Unit,
    private var onLeftKeyClicked: (View) -> Unit,
    private var onRightKeyClicked: (View) -> Unit
) : RecyclerView.Adapter<LocalAttractionGsAdapter.MyViewHolder>() {

    private var gradientDrawable: GradientDrawable? = null
    private var itemList = listOf<LAServices>()
    private var selectedPosition = -1

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

        view.setOnFocusChangeListener { v, b ->
            if (b) {
                v.background = gradientDrawable
            } else {
                v.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.textView.text = item.categoryName
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

        if (selectedPosition == -1){
            onItemClicked(holder.itemView, item)
            holder.itemView.isSelected = true
            selectedPosition = holder.absoluteAdapterPosition
        }
        updateFocus(holder)

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (position == 0) {
                if (hasFocus) {
                    view.nextFocusUpId = view.id
                } else {
                    view.nextFocusUpId = View.NO_ID
                }
            } else if (position == itemList.size.minus(1)) {
                if (hasFocus) {
                    view.nextFocusDownId = view.id
                } else {
                    view.nextFocusDownId = View.NO_ID
                }
            }
            if (hasFocus) {
                view.background = gradientDrawable
                view.setOnKeyListener { _, code, _ ->
                    when (code) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            onItemClicked(view, item)
                            holder.itemView.isSelected = true
                            selectedPosition = holder.absoluteAdapterPosition
                            updateFocus(holder)
                            notifyUI()
                        }

                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            onLeftKeyClicked(view)
                        }
                    }
                    false
                }
            } else {
                updateFocus(holder)
            }

        }

    }

    private fun onClick(code: Int, item: LAServices, holder: MyViewHolder) {

    }

    private fun notifyUI() {
        itemList.forEachIndexed { index, laServices ->
            if (selectedPosition != index)
                notifyItemChanged(index)
        }
    }

    private fun updateFocus(holder: MyViewHolder) {
        if (selectedPosition == holder.absoluteAdapterPosition && holder.itemView.isSelected) {
            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_spotlight)
        } else {
            holder.textView.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        }
    }

    fun setGradientDrawable(gradient: GradientDrawable) {
        gradientDrawable = gradient
    }

    fun setItemList(btnList: List<LAServices>) {
        itemList = btnList
    }


}