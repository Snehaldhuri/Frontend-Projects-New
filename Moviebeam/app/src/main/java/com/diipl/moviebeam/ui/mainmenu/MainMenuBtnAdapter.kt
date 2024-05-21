package com.diipl.moviebeam.ui.mainmenu

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.utils.handleFocusChange

private const val TAG = "MainMenuBtnAdapter"

class MainMenuBtnAdapter(
    private var onMenuItemClicked: (BtnModel) -> Unit
) :
    RecyclerView.Adapter<MainMenuBtnAdapter.MyViewHolder>() {
    var itemList: List<BtnModel> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_menu_icon)
        val textView: TextView = itemView.findViewById(R.id.tv_menu_title)
        val card: ConstraintLayout = itemView.findViewById(R.id.clHomeMenuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.imageView.setImageResource(item.imageResId)
        holder.textView.text = item.title

        /*
                holder.card.postDelayed(
                    {
                        if (holder.absoluteAdapterPosition == 0) {
                            holder.card.requestFocus()
                        }
                    },200
                )
        */
        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

        holder.card.handleFocusChange()
        holder.card.setOnClickListener {
            onMenuItemClicked(item)
        }

        holder.itemView.setOnKeyListener { view, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_TV_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_TV_INPUT")
            if (i == KeyEvent.KEYCODE_NAVIGATE_IN) Log.e(
                TAG,
                "onBindViewHolder: KEYCODE_NAVIGATE_IN"
            )
            if (i == KeyEvent.KEYCODE_AVR_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_AVR_INPUT")
            if (i == KeyEvent.KEYCODE_STB_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_STB_INPUT")
            Log.e(TAG, "onBindViewHolder: $i")
            false
        }

    }

}


