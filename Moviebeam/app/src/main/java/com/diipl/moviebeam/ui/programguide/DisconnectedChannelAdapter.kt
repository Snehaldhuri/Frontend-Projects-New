package com.diipl.moviebeam.ui.programguide

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.databinding.ChannelCardBinding
import com.diipl.moviebeam.databinding.DisconnectedChannelCardBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class DisconnectedChannelAdapter(private val onChannelFocused: (program: ChannelEpgDTO?) -> Unit,
                                 private val onChannelClicked: (program: ChannelEpgDTO?) -> Unit) :
    RecyclerView.Adapter<DisconnectedChannelAdapter.MyViewHolder>() {

    private var channelList: List<ChannelEpgDTO>? = emptyList()
    private var focusIndex = -1

    inner class MyViewHolder(val binding: DisconnectedChannelCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = DisconnectedChannelCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = channelList?.get(position)
        holder.binding.tvChannelNo.text = item?.CNO.toString()

        if (focusIndex == holder.absoluteAdapterPosition) {
            holder.binding.root.requestFocus()
        } else holder.binding.root.clearFocus()

        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                onChannelFocused(item)
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                holder.binding.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                holder.binding.tvChannelName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            } else {
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                holder.binding.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                holder.binding.tvChannelName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            }
        }
        holder.binding.root.setOnClickListener {
            onChannelClicked(item)
        }

            holder.binding.tvChannelName.text = item?.CN
    }

    override fun getItemCount(): Int = channelList?.size ?: 0



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