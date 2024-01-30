package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryDataList
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryRequestDTO
import com.diipl.moviebeam.data.dto.laundryResponce.SubCategoryList
import com.diipl.moviebeam.databinding.RecyclerLayoutLaundryBinding

class LaundryAdapter(
    private var onMenuItemFocused: (LaundryDataList) -> Unit,
    private val onLeftKeyPressed: () -> Unit,
    var onRightKeyPressed: () -> Unit,
) :
    RecyclerView.Adapter<LaundryAdapter.MyViewHolder>() {

    private var startColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var endColor = Constants.DEFAULTGRADIENTENDCOLOR


    private var gradient: GradientDrawable? = null
    private var laundryList: List<LaundryDataList> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RecyclerLayoutLaundryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.root.isFocusable = true
        binding.root.requestFocus()
        binding.root.isFocusableInTouchMode = true
        binding.root.setOnKeyListener { _, keycode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keycode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        onLeftKeyPressed()
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        onRightKeyPressed()
                    }
                }
            }
            false
        }
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return laundryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = laundryList[position]
        Log.e("item", "onBindViewHolder:${item}")
        holder.binding.tvLaundryType.text = item.categoryName


        holder.binding.clCardLaundry.setBackgroundResource(R.drawable.btn_bg_gradient_default)
        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            onMenuItemFocused(item)
            if (isFocused) {
                holder.binding.clCardLaundry.background = gradient
            } else {
                holder.binding.clCardLaundry.setBackgroundResource(R.drawable.btn_bg_gradient_default_5dp)
            }
        }
    }

    class MyViewHolder(val binding: RecyclerLayoutLaundryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }



    fun setNewsList(laundryDataList: List<LaundryDataList>) {
        this.laundryList = laundryDataList
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient = gradient
    }


}