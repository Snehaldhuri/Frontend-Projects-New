package com.diipl.moviebeam.ui.mainmenu

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.ui.hotelinfo.HotelInfoActivity

class MainMenuBtnAdapter() :
    RecyclerView.Adapter<MainMenuBtnAdapter.MyViewHolder>() {

    var cont: Context? = null

    var startColor = ""
    var endColor = ""

    var itemList: List<BtnModel> = mutableListOf()


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val card : ConstraintLayout = itemView.findViewById(R.id.card1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        cont = parent.context
        view.setOnClickListener{
            parent.context.startActivity(Intent(parent.context, HotelInfoActivity::class.java))
        }
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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

    fun setGradientColor(startColor:String,endColor :String){
        this.startColor=startColor
        this.endColor = endColor
    }

}


