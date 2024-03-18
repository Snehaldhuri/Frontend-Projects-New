package com.diipl.moviebeam.ui.hotelinfo


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
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.loadImagesWithGlideExtHsCard

private const val TAG = "MyCardPresenter"
class MyCardPresenter(
    private val onItemFocused: ((String)) -> Unit, private val onLeftKeyPressed: (String) -> Unit
) : Presenter() {

    private var focusedPosition = 0
    var rowLength = 0

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
//            if (service.description.contains("<br>") or service.description.contains("<br/>")) {
                description.text = Html.fromHtml(service.description)
//            } else description.text = service.description
//            cardView.text = service.description.replace("<br>", "", true)
            title.text = service.title
            if (service.serviceImageList.isNotEmpty()){
                imageview.loadImagesWithGlideExtHsCard(service.serviceImageList[0])
            }

            viewHolder.view.setOnKeyListener { _, keycode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keycode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (focusedPosition != 0) {
                                focusedPosition--
                            } else {
                                onLeftKeyPressed(service.title)
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (focusedPosition < rowLength-1){
                                focusedPosition++
                            }
                        }
                    }
                }
                false
            }

        }

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources when the view is unbound
    }

}

