package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataList
import com.diipl.moviebeam.data.dto.laundryResponce.LaundrySubCategory
import com.diipl.moviebeam.data.dto.laundryResponce.SubCategoryList
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
import com.diipl.moviebeam.databinding.CustomLaundryListViewBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.loadImagesWithGlideExtFomAssets
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLA


class CustomAdapterLaundry(
    var onMenuItemFocused: (LaundrySubCategory) -> Unit,
    var onLeftKeyPressed: () -> Unit?
    ) : RecyclerView.Adapter<CustomAdapterLaundry.MyViewHolder>() {

    var sublList: List<LaundrySubCategory> = emptyList()
    var gradient2: GradientDrawable? = null
    var count = 0
    private var laundryList: List<LaundryDataList> = emptyList()
    var binding: CustomLaundryListViewBinding? = null
    private lateinit var context: Context
    private val selectedItems: MutableList<LaundrySubCategory> = mutableListOf()
    private var startColor = ""
    private var endColor = ""

    companion object {
        var laundryConstSelected: MutableList<LaundrySubCategory> = mutableListOf()

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        context = parent.context
        val layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        holder.tv_lv_title.text = sublList[p0].title
        holder.tv_lv_price.text = sublList[p0].dispPrice
        holder.item_img.loadImagesWithGlideExtFomAssets(sublList[p0].imgSrc)

        holder.binding.root.setOnFocusChangeListener{ view, isFocused ->
            if (isFocused) {
                holder.binding.imgAdd.post{
                    holder.binding.imgAdd.requestFocus()
                }
                holder.binding.imgAdd.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        setImageFocus(holder.binding.imgAdd)
                        view.setOnKeyListener { _, keyCode, event ->
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                        holder.binding.imgRemove.post {
                                            holder.binding.imgRemove.requestFocus()
                                        }
                                        holder.binding.imgRemove.setOnFocusChangeListener { view, hasFocus ->
                                            if (hasFocus) {
                                                setImageFocus(holder.binding.imgRemove)
                                                view.setOnKeyListener { _, keyCode, event ->
                                                    if (event.action == KeyEvent.ACTION_DOWN) {
                                                        when (keyCode) {
                                                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                                                holder.binding.imgAdd.post {
                                                                    holder.binding.imgAdd.requestFocus()
                                                                }
                                                                holder.binding.imgAdd.setOnFocusChangeListener { view, hasFocus ->
                                                                    if (hasFocus) {
                                                                        setImageFocus(holder.binding.imgAdd)
                                                                    } else {
                                                                        holder.binding.imgAdd.setBackgroundResource(
                                                                            R.drawable.btn_bg_gradient_default_5dp
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    false
                                                }
                                            } else {
                                                holder.binding.imgRemove.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
                                            }
                                        }
                                        return@setOnKeyListener true
                                    }
                                }
                            }
                            false
                        }
                    }
                    else{
                        holder.binding.imgAdd.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
                    }
                }

            }
            else{
                holder.binding.imgAdd.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
            }
        }

        holder.binding.imgAdd.setOnClickListener {
            val existingItem = selectedItems.find { it.id == sublList[p0].id }
            if (existingItem != null) {
                existingItem.quantity += 1
                holder.binding.tvCount.text = existingItem.quantity.toString()
            } else {
                sublList[p0].quantity += 1
                holder.binding.tvCount.text = sublList[p0].quantity.toString()
                selectedItems.add(sublList[p0])
                laundryConstSelected.add(sublList[p0])
            }
        }

        holder.binding.imgRemove.setOnClickListener {
            if (sublList[p0].quantity > 0) {
                sublList[p0].quantity -= 1
                holder.binding.tvCount.text = sublList[p0].quantity.toString()
                if (sublList[p0].quantity == 0) {
                    // Remove item from selected items if quantity becomes zero
                    selectedItems.remove(sublList[p0])
                    laundryConstSelected.remove(sublList[p0])
                }
            }
        }
    }

    private fun setImageFocus(cardView: ImageView) {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
//        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        cardView.background = gradientDrawable
    }

    class MyViewHolder(val binding: CustomLaundryListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var convertView = binding.root
        var count = 0
        var tv_lv_title: TextView = convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_lv_title)
        var tv_lv_price: TextView = convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_lv_price)
        var item_img: ImageView = convertView.findViewById<ImageView>(R.id.iv_item)
        var img_add: ImageView = convertView.findViewById<ImageView>(com.diipl.moviebeam.R.id.img_add)
        var img_remove: ImageView = convertView.findViewById<ImageView>(com.diipl.moviebeam.R.id.img_remove)
        var tv_count: TextView = convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_count)
        var ll: LinearLayout = convertView.findViewById(R.id.ll_cart)

    }


    fun setNewsList(subCategoryList: List<LaundrySubCategory>) {
        this.sublList = subCategoryList
    }

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }
    fun setGradient(gradient: GradientDrawable) {
        this.gradient2 = gradient
    }
    fun getSelectedItems(): List<LaundrySubCategory> {
        return laundryConstSelected.toList()
    }
    fun clearSelectedItems() {
        laundryConstSelected.clear()
    }
}
