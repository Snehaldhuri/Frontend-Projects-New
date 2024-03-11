package com.diipl.moviebeam.ui.hotelinfo


import android.content.Context
import android.graphics.Color
import android.text.Html
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.leanback.widget.Presenter
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.hotelservice.Service
import com.diipl.moviebeam.utils.loadImagesWithGlideExtHsCard

private const val TAG = "MyCardPresenter"

class MyCardPresenter(
    private val onItemFocused: ((String)) -> Unit, private val onLeftKeyPressed: (String) -> Unit
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {

        val defaultColor = "#C0C0C0"
        val focusedColor = "#FFFFFF"
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.carousel_card, parent, false)

        view.isFocusable = true
        view.setOnFocusChangeListener { it, b ->
            onItemFocused(it.findViewById<TextView>(R.id.tv_card_title).text.toString())

            if (b) {
                it.findViewById<CardView>(R.id.card).background.setTint(
                    Color.parseColor(
                        focusedColor
                    )
                )
            } else {
                it.findViewById<CardView>(R.id.card).background.setTint(
                    Color.parseColor(
                        defaultColor
                    )
                )
            }
            view.setOnKeyListener { _, keycode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keycode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed(it.findViewById<TextView>(R.id.tv_card_title).text.toString())
                    }
                }
                false
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
            val description = viewHolder.view.findViewById<TextView>(R.id.tv_card_content)
            val title = viewHolder.view.findViewById<TextView>(R.id.tv_card_title)
            val imageview = viewHolder.view.findViewById<TextView>(R.id.iv_card_image) as ImageView

            // Set card content
            if (service.description.contains("<br/>")) {
                description.text = Html.fromHtml(service.description)
            } else description.text = service.description
//            cardView.text = service.description.replace("<br>", "", true)
            title.text = service.title
            imageview.loadImagesWithGlideExtHsCard(service.serviceImageList[0])
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

