package com.diipl.moviebeam.ui.guestservice.concierge

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.accountsetup.ItemMenu
import com.diipl.moviebeam.databinding.ItemToiletryRequestBinding
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class ToiletryRequestAdapter(
    private var onMenuItemClicked: (Boolean, ItemMenu) -> Unit
) : RecyclerView.Adapter<ToiletryRequestAdapter.MyViewHolder>() {

    private var itemList: List<ItemMenu> = mutableListOf()
    private var startColor = ""
    private var endColor = ""
    private val selectedItems: MutableList<ItemMenu> = mutableListOf()

    inner class MyViewHolder(val binding: ItemToiletryRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemToiletryRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = itemList[position]
        holder.binding.tvItem.text = item.name
        holder.binding.tvCharges.text = item.dispPrice


        holder.binding.clToiletryItems.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setFocus(holder.binding.clItem)

                view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        when (keyCode) {
                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                holder.binding.imgAdd.postDelayed(
                                    {
                                        holder.binding.imgAdd.requestFocus()
                                    }, 50
                                )
                                holder.binding.imgAdd.setOnFocusChangeListener { view, hasFocus ->
                                    if (hasFocus) {
                                        setImageFocus(holder.binding.imgAdd)
                                        view.setOnKeyListener { _, keyCode, event ->
                                            if (event.action == KeyEvent.ACTION_DOWN) {
                                                when (keyCode) {
                                                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                                        holder.binding.imgRemove.postDelayed(
                                                            {
                                                                holder.binding.imgRemove.requestFocus()
                                                            }, 50
                                                        )
                                                        holder.binding.imgRemove.setOnFocusChangeListener { view, hasFocus ->
                                                            if (hasFocus) {
                                                                setImageFocus(holder.binding.imgRemove)
                                                            } else {
                                                                holder.binding.imgRemove.setBackgroundResource(
                                                                    R.drawable.btn_bg_gradient_default
                                                                )
                                                            }
                                                        }
                                                        return@setOnKeyListener true
                                                    }

                                                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                                                        holder.binding.clToiletryItems.postDelayed({
                                                            holder.binding.clToiletryItems.requestFocus()
                                                        }, 50)
                                                        holder.binding.clToiletryItems.setOnFocusChangeListener { view, hasFocus ->
                                                            if (hasFocus) {
                                                                setFocus(holder.binding.clItem)
                                                            } else {
                                                                holder.binding.clItem.setBackgroundResource(
                                                                    R.color.transparent
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            false
                                        }
                                    } else {
                                        holder.binding.imgAdd.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                                    }
                                }
                                setImageFocus(holder.binding.imgAdd)
                                return@setOnKeyListener true
                            }
                        }
                    }
                    false
                }
            } else {
                holder.binding.clItem.setBackgroundResource(R.color.transparent)
            }
        }
        holder.binding.clToiletryItems.setOnClickListener {
            onMenuItemClicked(holder.binding.ivIconChecked.isVisible, item)
            if (holder.binding.ivIconChecked.isVisible) {
                holder.binding.ivIconChecked.toInvisible()
                holder.binding.clQuantity.toGone()
            } else {
                holder.binding.ivIconChecked.toVisible()
                holder.binding.clQuantity.toVisible()
            }
        }



        holder.binding.imgAdd.setOnClickListener {
            item.quantity += 1
            holder.binding.tvCount.text = item.quantity.toString()
        }
        holder.binding.imgRemove.setOnClickListener {
            if (item.quantity != 1) {
                item.quantity -= 1
                holder.binding.tvCount.text = item.quantity.toString()
            }
        }
        if (holder.binding.ivIconChecked.isVisible) {
            selectedItems.add(item)
        }
    }

    private fun setFocus(cardView: ConstraintLayout) {
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

    fun setButtonList(itemList: List<ItemMenu>) {
        this.itemList = itemList
    }

    fun setGradientColor(startColor: String, endColor: String) {
        this.startColor = startColor
        this.endColor = endColor
    }
}
