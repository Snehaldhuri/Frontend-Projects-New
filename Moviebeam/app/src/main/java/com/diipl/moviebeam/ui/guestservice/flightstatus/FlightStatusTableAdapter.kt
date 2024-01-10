package com.diipl.moviebeam.ui.guestservice.flightstatus

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.flightstatus.Flight

class FlightStatusTableAdapter(
    private var onFlightFocused: (View, Boolean) -> Unit,
    private val onLeftKeyPressed: () -> Unit
) : RecyclerView.Adapter<FlightStatusTableAdapter.MyViewHolder>() {

    private var flightList: List<Flight> = mutableListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightNo: TextView = itemView.findViewById(R.id.tv_flight_no)
        val destination: TextView = itemView.findViewById(R.id.tv_destination)
        val airlines: TextView = itemView.findViewById(R.id.tv_airlines)
        val time: TextView = itemView.findViewById(R.id.tv_time)
        val terminal: TextView = itemView.findViewById(R.id.tv_terminal)
        val gate: TextView = itemView.findViewById(R.id.tv_gate)
        val status: TextView = itemView.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flight_status_table_row, parent, false)
        view.isFocusable = true
        view.setOnFocusChangeListener { focusedView, focus ->
            onFlightFocused(focusedView, focus)
        }
        view.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
                }
            }
            false
        }

        val params = view.layoutParams
        params.height = getHeightInPercent(parent.context, 7)
        return MyViewHolder(view)
    }

    private fun getHeightInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.heightPixels ?: 0
        return (width * percent) / 100
    }

    override fun getItemCount(): Int = flightList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = flightList[position]

        setContent(holder.flightNo, item.flightNo)
        setContent(holder.destination, item.destination)
        setContent(holder.airlines, item.airline)
        setContent(holder.time, item.departureTime)
        setContent(holder.terminal, item.terminal)
        setContent(holder.gate, item.gate)
        if(item.colorCode != null){
            setContent(holder.status, item.status, item.colorCode)
        }else{
            setContent(holder.status, item.status)
        }

    }

    private fun setContent(textView: TextView, text: String?, color: String = "white") {
        if (text == null){
            textView.text = Constants.NOT_AVAILABLE
        }else{
            textView.text = text
        }
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        when (color) {
            "yellow" -> {
                textView.setTextColor(Color.parseColor(Constants.COLOR_YELLOW))
            }

            "orange" -> {
                textView.setTextColor(Color.parseColor(Constants.COLOR_ORANGE))
            }

            "green" -> {
                textView.setTextColor(Color.parseColor(Constants.COLOR_GREEN))
            }

            else -> {
                textView.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            }

        }

    }

    fun setFlightList(flightList: List<Flight>) {
        this.flightList = flightList
    }

}