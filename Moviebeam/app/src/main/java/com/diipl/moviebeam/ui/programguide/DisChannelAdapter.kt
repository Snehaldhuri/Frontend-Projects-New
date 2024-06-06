package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.program.ChannelEpgDTO
import com.diipl.moviebeam.databinding.DisconnectedChannelCardBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.setSafeOnClickListener

class DisChannelAdapter(
    private val onChannelClicked: (program: ChannelEpgDTO?) -> Unit
) :
    RecyclerView.Adapter<DisChannelAdapter.MyViewHolder>() {

    private var channelList: List<ChannelEpgDTO>? = null
    private var focusIndex = -1

    inner class MyViewHolder(val binding: DisconnectedChannelCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DisconnectedChannelCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val params = binding.root.layoutParams
        params.width = getWidthInPercent(parent.context, 22)
        params.height = getHeightInPercent(parent.context, 6)

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = channelList?.size ?: 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = channelList?.get(position)

        holder.binding.tvChannelNo.text = item?.CNO.toString()

        if (focusIndex == holder.absoluteAdapterPosition) {
            holder.binding.root.requestFocus()
        } else holder.binding.root.clearFocus()

        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                holder.binding.root.setCardBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                holder.binding.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                holder.binding.tvChannelName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            } else {
                holder.binding.root.setCardBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                holder.binding.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                holder.binding.tvChannelName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            }
        }
        holder.binding.root.setSafeOnClickListener {
            onChannelClicked(item)
        }
        holder.binding.tvChannelName.text = item?.CN
    }

    fun setChannelList(list: List<ChannelEpgDTO>?) {
        this.channelList = list
    }
    fun getChannelList(): List<ChannelEpgDTO>? {
        return channelList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFocus(focusIndex: Int) {
        this.focusIndex = focusIndex
        notifyDataSetChanged()
    }
}