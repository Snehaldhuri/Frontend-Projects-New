package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryResponce
import com.diipl.moviebeam.data.dto.laundryResponce.LaundrySubCategory
import com.diipl.moviebeam.databinding.ItemLaundryRequestSummaryBinding
import com.diipl.moviebeam.databinding.ItemToiletryRequestSummaryBinding
import com.diipl.moviebeam.ui.guestservice.concierge.ToiletryRequestSummaryAdapter

class LaundryRequestSummaryAdapter :
    RecyclerView.Adapter<LaundryRequestSummaryAdapter.MyViewHolder>() {

    private var itemList: List<LaundrySubCategory> = mutableListOf()

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

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvItems.text = item.title
        holder.binding.tvQty.text = item.quantity.toString()
    }


    fun setItemList(selectedItems: List<LaundrySubCategory>) {
        this.itemList = selectedItems
        Log.d("selecteditemslaundry3", "$selectedItems")
    }
}