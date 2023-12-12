package com.diipl.moviebeam.ui.hotelinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.HotelInfoBtnModel

class HotelInfoTabAdapter(private val itemList: List<String>, private var onItemClicked: ((String)) -> Unit) :
    RecyclerView.Adapter<HotelInfoTabAdapter.MyViewHolder>() {

    var cont: Context? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val card : ConstraintLayout = itemView.findViewById(R.id.card1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hotel_info_tab, parent, false)
        cont = parent.context
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.textView.text = item
        holder.card.setOnClickListener{
            Toast.makeText(cont, "Button : ${item}", Toast.LENGTH_SHORT).show()
        }

//        holder.card.setOnClickListener {
//            onItemClicked(itemList[position])
//        }

        holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)

        holder.card.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onItemClicked(itemList[position])
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_focus)
            } else {
                holder.card.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
    }
}
