package com.diipl.moviebeam.ui.programguide

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.dto.program.ProgramDTO
import com.diipl.moviebeam.databinding.ChannelCardBinding
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class ChannelAdapter(
    private val onChannelFocused: (program: ProgramDTO) -> Unit,
    private val onChannelClicked: (program: ProgramDTO) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.MyViewHolder>() {

    private var channelList: List<ProgramDTO> = emptyList()

    inner class MyViewHolder(val binding: ChannelCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChannelCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = channelList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = channelList[position]
        holder.binding.tvChannelNo.text = item.CNO.toString()
        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                onChannelFocused(item)
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                holder.binding.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            } else {
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                holder.binding.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            }
        }
        holder.binding.root.setOnClickListener {
            onChannelClicked(item)
        }
        item.CL?.let {
            holder.binding.ivChannelLogo.loadImagesWithGlideExt(it)
        }
    }

    fun setChannelList(list: List<ProgramDTO>) {
        this.channelList = list
    }

}