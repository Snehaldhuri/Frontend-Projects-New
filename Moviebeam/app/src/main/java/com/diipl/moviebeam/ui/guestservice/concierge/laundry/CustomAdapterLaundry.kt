package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.KeyEvent
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
import com.diipl.moviebeam.data.dto.laundryResponce.SubCategoryList
import com.diipl.moviebeam.databinding.CustomLaundryListViewBinding


class CustomAdapterLaundry(
    var onMenuItemFocused: (SubCategoryList) -> Unit,
    var onLeftKeyPressed: () -> Unit?,
    var context: Context,
    var array_title: ArrayList<String>,
    var array_price: ArrayList<String>
) : BaseAdapter() {

    var sublList: List<SubCategoryList> = emptyList()

    var gradient2: GradientDrawable? = null

    override fun getCount(): Int {
        return sublList.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View {


        val layoutInflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = CustomLaundryListViewBinding.inflate(layoutInflater, parent, false)

        binding.root.isFocusable = true
        binding.root.postDelayed({ binding.root.requestFocus() }, 50000)

        binding.root.isFocusableInTouchMode = true
        binding.root.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        onLeftKeyPressed()
                    }
                }
            }
            false
        }



        if (convertView == null) {

            var convertView = binding.root
            var count = 1;
            var tv_lv_title: TextView =
                convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_lv_title)
            var tv_lv_price: TextView =
                convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_lv_price)
            var img_add: ImageView =
                convertView.findViewById<ImageView>(com.diipl.moviebeam.R.id.img_add)
            var img_remove: ImageView =
                convertView.findViewById<ImageView>(com.diipl.moviebeam.R.id.img_remove)
            var tv_count: TextView =
                convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_count)


            var ll: LinearLayout = convertView.findViewById(R.id.ll_cart)

            Log.e("sublist", "getView:${sublList.size} ")
            tv_lv_title.text = array_title[p0]
            tv_lv_price.text = array_price[p0]
            var hide: Boolean = true;

            tv_lv_title.setOnClickListener(View.OnClickListener {

                var constraintset: ConstraintSet = ConstraintSet()
                constraintset.clone(context, com.diipl.moviebeam.R.layout.custom_laundry_list_view)

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

            val item = sublList[p0]
            convertView.setOnFocusChangeListener { view, isFocused ->
                onMenuItemFocused(item)
                if (isFocused) {
                    convertView.background = gradient2
                } else {
                    // convertView.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
                }
            }
        }
        return binding.root
    }

    fun setNewsList(subCategoryList: ArrayList<SubCategoryList>) {
        this.sublList = subCategoryList
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient2 = gradient
    }
}