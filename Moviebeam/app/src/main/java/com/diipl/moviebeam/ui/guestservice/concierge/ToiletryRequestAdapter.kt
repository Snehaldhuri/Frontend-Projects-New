package com.diipl.moviebeam.ui.guestservice.concierge

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.ItemMenu
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryCategory
import com.diipl.moviebeam.data.dto.laundryResponce.LaundrySubCategory
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
import com.diipl.moviebeam.databinding.ItemToiletryRequestBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExtFomAssets
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class ToiletryRequestAdapter(
    private var onMenuItemClicked: (Boolean, ToiletryResponse.ToiletryData) -> Unit,
    private var onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<ToiletryRequestAdapter.MyViewHolder>() {

    private var itemList: List<ItemMenu> = mutableListOf()
    private var startColor = ""
    private var endColor = ""
    private val selectedItems: MutableList<ToiletryResponse.ToiletryData> = mutableListOf()
    private var toiletryList: List<ToiletryResponse.ToiletryData> = emptyList()
    inner class MyViewHolder(val binding: ItemToiletryRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemToiletryRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = toiletryList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = toiletryList[position]
        Log.e("toiletrylist", "getView:${toiletryList.size} ")
        holder.binding.tvLvTitle.text = item.name
        holder.binding.ivItem.loadImagesWithGlideExtFomAssets(item.imgSrc)
        holder.binding.tvCount.text = item.quantity.toString()
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

        holder.binding.toiletryData.setOnClickListener {
//            onMenuItemClicked(holder.binding.ivIconChecked.isVisible, item)
//            if (holder.binding.ivIconChecked.isVisible) {
//                holder.binding.ivIconChecked.toInvisible()
//                holder.binding.clQuantity.toGone()
//            } else {
//                holder.binding.ivIconChecked.toVisible()
//                holder.binding.clQuantity.toVisible()
//            }
        }

        holder.binding.imgAdd.setOnClickListener {
            val existingItem = selectedItems.find { it.id == item.id }
            if (existingItem != null) {
                existingItem.quantity += 1
                holder.binding.tvCount.text = existingItem.quantity.toString()
            } else {
                item.quantity += 1
                holder.binding.tvCount.text = item.quantity.toString()
                selectedItems.add(item)
            }
        }

        holder.binding.imgRemove.setOnClickListener {
            if (item.quantity > 0) {
                item.quantity -= 1
                holder.binding.tvCount.text = item.quantity.toString()
                if (item.quantity == 0) {
                    // Remove item from selected items if quantity becomes zero
                    selectedItems.remove(item)
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

    fun setToiletryList(toiletryList: List<ToiletryResponse.ToiletryData>) {
        this.toiletryList = toiletryList
//        selectedItems.clear()
//        selectedItems.addAll(toiletryList.filter { it.quantity > 0 })
    }

    fun getSelectedItems(): List<ToiletryResponse.ToiletryData> {
        return selectedItems.toList()
    }
    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }
}
