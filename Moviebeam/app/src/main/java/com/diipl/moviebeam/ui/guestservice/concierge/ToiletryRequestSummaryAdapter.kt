package com.diipl.moviebeam.ui.guestservice.concierge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.accountsetup.ItemMenu
import com.diipl.moviebeam.databinding.ItemToiletryRequestSummaryBinding


class ToiletryRequestSummaryAdapter: RecyclerView.Adapter<ToiletryRequestSummaryAdapter.MyViewHolder>() {

    private var itemList: List<ItemMenu> = mutableListOf()
    var totalCharge = 0f
    inner class MyViewHolder(val binding: ItemToiletryRequestSummaryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemToiletryRequestSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (itemList.isNotEmpty() && position < itemList.size) {
            val item = itemList[position]
            holder.binding.tvItems.text = item.name
            holder.binding.tvQty.text = item.quantity.toString()
            holder.binding.tvCost.text = item.dispPrice

            val numericPart = item.dispPrice.replace("[^\\d.]".toRegex(), "")

            val cost = numericPart.toFloatOrNull() ?: 0f

            val quantity = item.quantity
            val total = quantity * cost

            holder.binding.tvTotal.text = "$" + total.toString()
        }
    }

    fun setItemList(selectedItems: MutableList<ItemMenu>){
        this.itemList = selectedItems
    }
}