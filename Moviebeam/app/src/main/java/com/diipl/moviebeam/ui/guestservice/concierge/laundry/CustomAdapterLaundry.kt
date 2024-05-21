package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundrySubCategory
import com.diipl.moviebeam.databinding.CustomLaundryListViewBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.loadImagesWithGlideExtFomAssets


class CustomAdapterLaundry(
    var onMenuItemFocused: (LaundrySubCategory) -> Unit,
    var onLeftKeyPressed: () -> Unit?
) : RecyclerView.Adapter<CustomAdapterLaundry.MyViewHolder>() {

    var sublList: List<LaundrySubCategory> = emptyList()
    var count = 0
    var binding: CustomLaundryListViewBinding? = null
    private lateinit var context: Context
    private val selectedItems: MutableList<LaundrySubCategory> = mutableListOf()

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

        val item = sublList[p0]

        holder.tv_lv_title.text = sublList[p0].title
        holder.tv_lv_price.text = sublList[p0].dispPrice
        holder.item_img.loadImagesWithGlideExtFomAssets(sublList[p0].imgSrc)


        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                holder.binding.imgAdd.post {
                    holder.binding.imgAdd.requestFocus()
                }
                holder.binding.imgAdd.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        view.background = getGradientColor()
                        view.setOnKeyListener { _, keyCode, event ->
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                        holder.binding.imgRemove.post {
                                            holder.binding.imgRemove.requestFocus()
                                        }
                                        holder.binding.imgRemove.setOnFocusChangeListener { view, hasFocus ->
                                            if (hasFocus) {
                                                holder.binding.imgRemove.background = getGradientColor()
                                                view.setOnKeyListener { _, keyCode, event ->
                                                    if (event.action == KeyEvent.ACTION_DOWN) {
                                                        when (keyCode) {
                                                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                                                holder.binding.imgAdd.post {
                                                                    holder.binding.imgAdd.requestFocus()
                                                                }
                                                                holder.binding.imgAdd.setOnFocusChangeListener { view, hasFocus ->
                                                                    if (hasFocus) {
                                                                        holder.binding.imgAdd.background = getGradientColor()
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
                    } else {
                        holder.binding.imgAdd.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
                    }
                }

            } else {
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

    class MyViewHolder(val binding: CustomLaundryListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var convertView = binding.root
        var count = 0
        var tv_lv_title: TextView =
            convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_lv_title)
        var tv_lv_price: TextView =
            convertView.findViewById<TextView>(com.diipl.moviebeam.R.id.tv_lv_price)
        var item_img: ImageView = convertView.findViewById<ImageView>(R.id.iv_item)
        var ll: LinearLayout = convertView.findViewById(R.id.ll_cart)

    }


    fun setNewsList(subCategoryList: List<LaundrySubCategory>) {
        this.sublList = subCategoryList
    }

    fun getSelectedItems(): List<LaundrySubCategory> {
        return laundryConstSelected.toList()
    }

    fun clearSelectedItems() {
        laundryConstSelected.clear()
    }
}
