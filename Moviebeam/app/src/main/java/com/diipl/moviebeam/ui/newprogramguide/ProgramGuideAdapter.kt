package com.diipl.moviebeam.ui.newprogramguide

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.databinding.NewProgramGuideItemBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.setSafeOnClickListener
import com.diipl.moviebeam.utils.toGone
import com.diipl.moviebeam.utils.toVisible


class ProgramGuideAdapter(
    private val onChannelFocused: (program: ChannelEpgDTO?) -> Unit,
    private val updateProgramAndChannelData: (program: ChannelEpgDTO?, title: String?, synopsis: String?) -> Unit,
    private val loadNewPrograms: (isNextOrPrevious: Int, currentPosition: Int) -> Unit,
    private val onChannelClicked: (program: ChannelEpgDTO?) -> Unit,
) : RecyclerView.Adapter<ProgramGuideAdapter.MyViewHolder>() {

    private var channelFocusIndex = -1

    private var programList: List<ChannelEpgDTO>? = emptyList()
    private var p4Dst: String? = null

    private var focusedAdapter: Int = -1

    inner class MyViewHolder(val binding: NewProgramGuideItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            NewProgramGuideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val params = binding.root.layoutParams
        params.height = getHeightInPercent(parent.context, 4)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = programList?.get(position)

        if (item != null) {
            holder.binding.layoutChannelCard.tvChannelName.text = item.CN
            holder.binding.layoutChannelCard.tvChannelNo.text = item.CNO
        }

        if (channelFocusIndex == holder.adapterPosition) {
            holder.binding.root.requestFocus()
        } else holder.binding.root.clearFocus()

        if (focusedAdapter != -1 && focusedAdapter == holder.adapterPosition)
            holder.binding.rvProgramGuidePrograms.post {
                holder.binding.rvProgramGuidePrograms.getChildAt(0).requestFocus()
            }

        if (item != null) {
            if (item.CL != null) {
                holder.binding.layoutChannelCard.ivChannelLogo.loadImagesWithGlideExt(item.CL)
            } else {
                holder.binding.layoutChannelCard.ivChannelLogo.toGone()
                holder.binding.layoutChannelCard.tvChannelName.toVisible()
            }
        }

        holder.binding.layoutChannelCard.root.setSafeOnClickListener {
            onChannelClicked(item)
        }

        holder.binding.layoutChannelCard.root.setOnFocusChangeListener { view, focused ->
            if (focused) {
                onChannelFocused(item)
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                holder.binding.layoutChannelCard.tvChannelName.setTextColor(
                    Color.parseColor(
                        Constants.COLOR_BLACK
                    )
                )
                holder.binding.layoutChannelCard.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            } else {
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                holder.binding.layoutChannelCard.tvChannelName.setTextColor(
                    Color.parseColor(
                        Constants.COLOR_WHITE
                    )
                )
                holder.binding.layoutChannelCard.tvChannelNo.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            }
        }

        if (item != null) {
            setProgramsAdapter(holder, item)
        }
    }

    private fun setProgramsAdapter(holder: MyViewHolder, item: ChannelEpgDTO) {
        val adapter =
            NewProgramAdapter(
                onProgramFocused = ::onProgramFocused,
                onProgramClicked = ::onProgramClicked,
                loadNewPrograms = {
                    loadNextPrograms(it, holder.adapterPosition)
                }
            )
        adapter.setProgramDto(item)
        holder.binding.rvProgramGuidePrograms.adapter = adapter
    }

    private fun onProgramFocused(item: ChannelEpgDTO, title: String?, synopsis: String?) {

        updateProgramAndChannelData(item, title, synopsis)
    }

    private fun onProgramClicked(item: ChannelEpgDTO) {
        onChannelClicked(item)
    }

    private fun loadNextPrograms(isNextOrPrevious: Int, position: Int) {
        //1 for previous and 2 for next
        loadNewPrograms(isNextOrPrevious, position)
    }

    fun setProgramList(list: List<ChannelEpgDTO>?) {
        this.programList = list
    }

    fun setProg4Dst(p4Dst: String?) {
        this.p4Dst = p4Dst
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFocusOnSearch(channelFocusIndex: Int) {
        //focus on channel when search option is used
        this.channelFocusIndex = channelFocusIndex
        logE("updateFocusOnSearch")
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = programList?.size ?: 0

    fun updateChannelFocus(
        focusedPosition: Int,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val holder = viewHolder as ProgramGuideAdapter.MyViewHolder
        holder.binding.rvProgramGuidePrograms.apply {
            post {
                getChildAt(focusedPosition).requestFocus()
            }
        }
    }

    fun updateProgramFocus(focusedPosition: Int) {
        focusedAdapter = focusedPosition
    }
}
