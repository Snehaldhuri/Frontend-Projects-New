package com.diipl.moviebeam.ui.guestservice

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.flightstatus.Flight

class FlightStatusTableAdapter(
    private var onFlightFocused: (View, Boolean) -> Unit
) : RecyclerView.Adapter<FlightStatusTableAdapter.MyViewHolder>() {

    private var itemList: List<Flight> = mutableListOf()

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
        val params = view.layoutParams
        params.height = getHeightInPercent(parent.context, 7)
        return MyViewHolder(view)
    }

    private fun getHeightInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.heightPixels ?: 0
        return (width * percent) / 100
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        setContent(holder.flightNo, item.flightNo)
        setContent(holder.destination, item.destination)
        setContent(holder.airlines, item.airline)
        setContent(holder.time, item.departureTime)
        setContent(holder.terminal, item.terminal)
        setContent(holder.gate, item.gate)
        setContent(holder.status, item.status, item.colorCode)

    }

    private fun setContent(textView: TextView, text: String, color: String = "white") {
        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        when (color) {
            "yellow" -> {
                textView.setTextColor(Color.parseColor("#FFFF00"))
            }

            "orange" -> {
                textView.setTextColor(Color.parseColor("#FFA500"))
            }

            "green" -> {
                textView.setTextColor(Color.parseColor("#008000"))
            }

            else -> {
                textView.setTextColor(Color.parseColor("#FFFFFF"))
            }

        }

    }

    fun setFlightList(flightList: List<Flight>) {
        itemList = flightList
    }

}