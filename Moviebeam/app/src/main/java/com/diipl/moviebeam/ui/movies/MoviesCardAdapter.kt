package com.diipl.moviebeam.ui.movies

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.localattraction.LAService
import com.diipl.moviebeam.data.dto.movies.FreeContent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class MoviesCardAdapter( private var onMenuItemClicked: (String) -> Unit):
RecyclerView.Adapter<MoviesCardAdapter.MyViewHolder>() {

    private val defaultColor = "#FFFFFF"
    private var gradientDrawable: GradientDrawable? = null

    private var itemList: List<FreeContent> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_la_card_image)
        val textView: TextView = itemView.findViewById(R.id.tv_la_card_title)
        val frontCard: CardView = itemView.findViewById(R.id.front_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.la_carousel, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.imageView.loadImagesWithGlideExt(item.imagePathPoster)
        holder.textView.text = item.movieName

        holder.frontCard.visibility = View.VISIBLE
    }
    fun setList(itemList: List<FreeContent>) {
        this.itemList = itemList
    }

}