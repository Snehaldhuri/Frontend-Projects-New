package com.diipl.moviebeam.ui.programguide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.program.ProgramDTO
import com.diipl.moviebeam.databinding.ProgramsCardBinding

class ProgramsAdapter(
    private val onProgramFocused: (program: ProgramDTO) -> Unit,
    private val onProgramClicked: (program: ProgramDTO) -> Unit
) : RecyclerView.Adapter<ProgramsAdapter.MyViewHolder>() {

    private var programList: List<ProgramDTO> = emptyList()
    private var p4Dst: String? = null

    inner class MyViewHolder(val binding: ProgramsCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ProgramsCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.rvPrograms.layoutManager =
            LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = programList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = programList[position]
        val adapter =
            ProgramAdapter(onProgramFocused = onProgramFocused, onProgramClicked = onProgramClicked)
        adapter.setProg4Dst(p4Dst)
        adapter.setProgramDto(item)
        holder.binding.rvPrograms.adapter = adapter
    }

    fun setProgramList(list: List<ProgramDTO>) {
        this.programList = list
    }

    fun setProg4Dst(p4Dst: String) {
        this.p4Dst = p4Dst
    }

}