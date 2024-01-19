package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.diipl.moviebeam.R

class CustomAdapterLaundry(
    var context: Context,
    var array_title: ArrayList<String>,
    var array_price: ArrayList<String>
) : BaseAdapter() {


    override fun getCount(): Int {
        return array_title.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View {

        var convertView = convertView
        convertView =
            LayoutInflater.from(context).inflate(R.layout.custom_laundry_list_view, parent, false)
        var tv_lv_title: TextView = convertView.findViewById<TextView>(R.id.tv_lv_title)
        var tv_lv_price: TextView = convertView.findViewById<TextView>(R.id.tv_lv_price)
        var ll: LinearLayout = convertView.findViewById(R.id.ll_cart)

        tv_lv_title.text = array_title[p0]
        tv_lv_price.text = array_price[p0]


        var hide: Boolean = true


            tv_lv_title.setOnClickListener(View.OnClickListener {

                if(hide==true){
                    ll.visibility = View.VISIBLE
                    hide = false
                }
                else{
                    ll.visibility = View.GONE
                    hide = true
                }
            })

        return convertView
    }
}