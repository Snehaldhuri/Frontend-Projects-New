package com.diipl.moviebeam.ui.hotelinfo


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.hotelservice.Service
import com.diipl.moviebeam.utils.loadImagesWithGlideExt


class MyCardPresenter(private val onItemClicked: ((String)) -> Unit) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {

        val defaultColor = "#C0C0C0"
        val focusedColor = "#FFFFFF"
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.carousel_card, parent, false)


        view.isFocusable = true
        view.setOnFocusChangeListener { it, b ->
            onItemClicked(it.findViewById<TextView>(R.id.tv_card_title).text.toString())
            if(b){
                it.findViewById<CardView>(R.id.card).background.setTint(Color.parseColor(focusedColor))
            }else{
                it.findViewById<CardView>(R.id.card).background.setTint(Color.parseColor(defaultColor))
            }
        }

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 30)
        params.height = getHeightInPercent(parent.context, 60)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        if (item is Service) {
            val service: Service = item
            val cardView = viewHolder.view
            // Set card content
            cardView.findViewById<TextView>(R.id.tv_card_content).text = service.description.replace("<br>","", true)
            cardView.findViewById<TextView>(R.id.tv_card_title).text = service.title
            // Customize other card attributes as needed
            val imageview = cardView.findViewById<ImageView>(R.id.iv_card_image)

            imageview.loadImagesWithGlideExt(service.serviceImageList[0])
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources when the view is unbound
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

