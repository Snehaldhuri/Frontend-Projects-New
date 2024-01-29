package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataList
import com.diipl.moviebeam.data.dto.laundryResponce.SubCategoryList
import com.diipl.moviebeam.databinding.CustomLaundryListViewBinding


class CustomAdapterLaundry(
    var onMenuItemFocused: (SubCategoryList) -> Unit,
    var onLeftKeyPressed: () -> Unit?,
    var context: Context,
    var array_title: ArrayList<String>,
    var array_price: ArrayList<Double>,

    ) : RecyclerView.Adapter<CustomAdapterLaundry.MyViewHolder>() {

    var sublList: List<SubCategoryList> = emptyList()
    var gradient2: GradientDrawable? = null
    var count = 1
    private var laundryList: List<LaundryDataList> = emptyList()
    var binding: CustomLaundryListViewBinding? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val layoutInflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CustomLaundryListViewBinding.inflate(layoutInflater, parent, false)


        binding!!.root.isFocusable = true
        //   binding.root.postDelayed({ binding.root.requestFocus() }, 1000)
        binding!!.root.isFocusableInTouchMode = true
        binding!!.root.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        onLeftKeyPressed()
                    }
                }
            }
            false
        }
        return MyViewHolder(binding!!)
    }

    override fun getItemCount(): Int {
        return sublList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, p0: Int) {

        Log.e("sublist", "getView:${sublList.size} ")
        holder.tv_lv_title.text = array_title[p0]
        holder.tv_lv_price.text = array_price[p0].toString()
        var hide: Boolean = true
        val item = sublList[p0]

        holder.binding.root.setOnClickListener(View.OnClickListener {

            var constraintset: ConstraintSet = ConstraintSet()
            constraintset.clone(context, R.layout.custom_laundry_list_view)

            if (hide == true) {
                holder.ll.visibility = View.VISIBLE
                holder.binding.ivIconChecked.visibility = View.VISIBLE
                hide = false
                val params: ConstraintLayout.LayoutParams =
                    holder.tv_lv_title.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(100, 0, 200, 0)
                holder.tv_lv_title.layoutParams = params

                //function focus for left key pressed on selection of this item
             //   focus(onLeftKeyPressed = {holder.tv_lv_title.id},item,holder)

            } else {
                holder.ll.visibility = View.GONE
                hide = true
                holder.binding.ivIconChecked.visibility = View.GONE
                val params: ConstraintLayout.LayoutParams =
                    holder.tv_lv_title.layoutParams as ConstraintLayout.LayoutParams
                params.setMargins(0, 0, 550, 0)
                holder.tv_lv_title.layoutParams = params
            }
        })

        holder.img_add.setOnClickListener(View.OnClickListener {
            count += 1
            holder.tv_count.text = count.toString()
        })
        holder.img_remove.setOnClickListener(View.OnClickListener {
            if (count == 1) {
            } else {
                count -= 1
                holder.tv_count.text = count.toString()
            }
        })


        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            onMenuItemFocused(item)
            if (isFocused) {
                holder.tv_lv_title.background = gradient2
            } else {
                holder.tv_lv_title.setBackgroundResource(R.color.transparent)
            }
        }
    }




    class MyViewHolder(val binding: CustomLaundryListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var convertView = binding.root
        var count = 1
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
    }


    fun setNewsList(subCategoryList: ArrayList<SubCategoryList>) {
        this.sublList = subCategoryList
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient2 = gradient
    }
}
