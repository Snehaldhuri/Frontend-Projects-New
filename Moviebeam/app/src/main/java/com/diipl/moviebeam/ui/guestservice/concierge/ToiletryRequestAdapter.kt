package com.diipl.moviebeam.ui.guestservice.concierge

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.toiletryResponse.ToiletryResponse
import com.diipl.moviebeam.databinding.ItemToiletryRequestBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadImagesWithGlideExtFomAssets

class ToiletryRequestAdapter(
    private var onMenuItemClicked: (Boolean, ToiletryResponse.ToiletryData) -> Unit,
    private var onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<ToiletryRequestAdapter.MyViewHolder>() {

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
        holder.binding.tvLvTitle.text = item.name
        holder.binding.ivItem.loadImagesWithGlideExtFomAssets(item.imgSrc)
        holder.binding.tvCount.text = item.quantity.toString()
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
                                                view.background = getGradientColor()
                                                view.setOnKeyListener { _, keyCode, event ->
                                                    if (event.action == KeyEvent.ACTION_DOWN) {
                                                        when (keyCode) {
                                                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                                                holder.binding.imgAdd.post {
                                                                    holder.binding.imgAdd.requestFocus()
                                                                }
                                                                holder.binding.imgAdd.handleFocusChange()
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

    fun setToiletryList(toiletryList: List<ToiletryResponse.ToiletryData>) {
        this.toiletryList = toiletryList
    }

    fun getSelectedItems(): List<ToiletryResponse.ToiletryData> {
        return selectedItems.toList()
    }

}
