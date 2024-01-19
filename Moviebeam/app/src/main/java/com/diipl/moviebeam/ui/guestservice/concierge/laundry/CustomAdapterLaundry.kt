package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
        var count = 1;
        convertView =
            LayoutInflater.from(context).inflate(R.layout.custom_laundry_list_view, parent, false)
        var tv_lv_title: TextView = convertView.findViewById<TextView>(R.id.tv_lv_title)
        var tv_lv_price: TextView = convertView.findViewById<TextView>(R.id.tv_lv_price)
        var img_add: ImageView = convertView.findViewById<ImageView>(R.id.img_add)
        var img_remove: ImageView = convertView.findViewById<ImageView>(R.id.img_remove)
        var tv_count: TextView = convertView.findViewById<TextView>(R.id.tv_count)


        var ll: LinearLayout = convertView.findViewById(R.id.ll_cart)

        tv_lv_title.text = array_title[p0]
        tv_lv_price.text = array_price[p0]


        var hide: Boolean = true;


        tv_lv_title.setOnClickListener(View.OnClickListener {

            var constraintset: ConstraintSet = ConstraintSet()
            constraintset.clone(context, R.layout.custom_laundry_list_view)

            if (hide == true) {
                ll.visibility = View.VISIBLE
                hide = false

                val params: ConstraintLayout.LayoutParams =
                    tv_lv_title.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, 0, 260, 0)
                tv_lv_title.setLayoutParams(params)


            } else {
                ll.visibility = View.GONE
                hide = true

                val params: ConstraintLayout.LayoutParams =
                    tv_lv_title.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, 0, 500, 0)
                tv_lv_title.setLayoutParams(params)
            }
        })

        img_add.setOnClickListener(View.OnClickListener {
            count += 1
            tv_count.text = count.toString()
        })
        img_remove.setOnClickListener(View.OnClickListener {
            if (count == 1) {

            } else {
                count -= 1
                tv_count.text = count.toString()
            }
        })


        return convertView
    }
}