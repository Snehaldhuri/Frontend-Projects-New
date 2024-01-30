package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.laundryResponce.SubCategoryList
import com.diipl.moviebeam.databinding.ItemLaundryRequestSummaryBinding
import com.diipl.moviebeam.databinding.ItemToiletryRequestSummaryBinding

class LaundryRequestSummaryAdapter :
    RecyclerView.Adapter<LaundryRequestSummaryAdapter.MyViewHolder>() {

    private var itemList: List<SubCategoryList> = mutableListOf()

    inner class MyViewHolder(val binding: ItemLaundryRequestSummaryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemLaundryRequestSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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
            holder.binding.tvItems.text = item.title
            holder.binding.tvQty.text = item.dispPrice
        }
    }

    fun setItemList(selectedItems: MutableList<SubCategoryList>) {
        this.itemList = selectedItems
    }
}