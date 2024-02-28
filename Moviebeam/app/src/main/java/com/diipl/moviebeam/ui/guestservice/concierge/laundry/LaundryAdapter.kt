package com.diipl.moviebeam.ui.guestservice.concierge.laundry

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.laundryResponce.LaundryCategory
import com.diipl.moviebeam.databinding.RecyclerLayoutLaundryBinding

class LaundryAdapter(
    private var onMenuItemFocused: (LaundryCategory) -> Unit,
    private val onLeftKeyPressed: () -> Unit,
    var onRightKeyPressed: () -> Unit,
) :
    RecyclerView.Adapter<LaundryAdapter.MyViewHolder>() {

    private var gradient: GradientDrawable? = null
    private var laundryList: List<LaundryCategory> = emptyList()

    inner class MyViewHolder(val binding: RecyclerLayoutLaundryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RecyclerLayoutLaundryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return laundryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        binding.root.setOnKeyListener { _, keycode, keyEvent ->
//            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
//                when (keycode) {
//                    KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
//                    KeyEvent.KEYCODE_DPAD_RIGHT -> onRightKeyPressed()
//                }
//            }
//            false
//        }

        val item = laundryList[position]
        Log.e("item", "onBindViewHolder:${item}")
        holder.binding.tvLaundryType.text = item.categoryName
        holder.binding.clMainCardLaundry.postDelayed({
            if (position == 0) {
                holder.binding.clMainCardLaundry.requestFocus()
            }
        }, 1)

        holder.binding.clMainCardLaundry.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                onMenuItemFocused(item)
                view.background = gradient
                if (position == 0) {
                    if (hasFocus) {
                        view.nextFocusUpId = view.id
                    } else {
                        view.nextFocusUpId = View.NO_ID
                    }
                } else if (position == laundryList.size.minus(1)) {
                    if (hasFocus) {
                        view.nextFocusDownId = view.id
                    } else {
                        view.nextFocusDownId = View.NO_ID
                    }
                }

            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }


    }

    fun setLaundryList(laundryDataList: List<LaundryCategory>) {
        this.laundryList = laundryDataList
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient = gradient
    }


}