package com.diipl.moviebeam.ui.programguide

import android.content.Context
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.dto.program.ProgramDTO
import com.diipl.moviebeam.databinding.ProgramCardBinding
import kotlin.math.roundToInt

class ProgramAdapter(
    private val onProgramFocused: (program: ProgramDTO) -> Unit,
    private val onProgramClicked: (program: ProgramDTO) -> Unit
) : RecyclerView.Adapter<ProgramAdapter.MyViewHolder>() {

    private var programDto: ProgramDTO? = null
    private var programs = 0
    private var p4Dst: String? = null

    inner class MyViewHolder(val binding: ProgramCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ProgramCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true
        binding.root.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                programDto?.let {
                    onProgramFocused(it)
                }
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
            } else {
                view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
            }
        }
        binding.root.setOnClickListener {
            programDto?.let {
                onProgramClicked(it)
            }
        }
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = programs

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (position) {
            0 -> {
                setProgramTitle(holder.binding.programName, programDto?.P1_PT)
                setProgramWidth(holder.binding.root, programDto?.P1_CLS)
            }

            1 -> {
                setProgramTitle(holder.binding.programName, programDto?.P2_PT)
                setProgramWidth(holder.binding.root, programDto?.P2_CLS)
            }

            2 -> {
                setProgramTitle(holder.binding.programName, programDto?.P3_PT)
                setProgramWidth(holder.binding.root, programDto?.P3_CLS)
            }

            3 -> {
                setProgramTitle(holder.binding.programName, programDto?.P4_PT)
                setProgramWidth(holder.binding.root, programDto?.P4_CLS)
            }
        }
        if (position == programs - 1) {
            holder.binding.root.setOnKeyListener { _, keycode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keycode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {}
                    }
                }
                false
            }
        }
    }

    private fun setProgramWidth(view: View, percentStr: String?) {
        val percent = getWidthInPercent(view.context, extractPercent(percentStr))
        val params = view.layoutParams
        params.width = percent
    }

    private fun extractPercent(width: String?): Float {
        var percent = 0F
        val startIndex = width?.indexOf(":")
        val endIndex = width?.indexOf("%")
        if (startIndex != null && endIndex != null) {
            percent = width.substring(startIndex + 1, endIndex).toFloat()
        }
//        if(percent>20 && percent<21){
//            percent = 19.85F
//        }
//        if(percent>20 && percent<21){
//            percent = 20.1F
//        }
        return percent
    }

    private fun calculatePercent(percent: Float): Float {
        return (68 * percent) / 100
    }

    private fun getWidthInPercent(context: Context, percent: Float): Int {
        val width = context.resources.displayMetrics.widthPixels
        return ((width * calculatePercent(percent)) / 100).roundToInt()
    }

    private fun setProgramTitle(textView: TextView, title: String?) {
        textView.text = title
    }

    fun setProg4Dst(p4Dst: String?) {
        this.p4Dst = p4Dst
    }

    fun setProgramDto(programDTO: ProgramDTO) {
        this.programDto = programDTO
        if (programDto?.C != null) {
            if (programDto?.C?.toInt()!! > 4) {
                if (programDto?.P5_ST == p4Dst)
                    programs = 5
                else if (programDto?.P4_ID != null)
                    programs = 4
                else if (programDto?.P3_ID != null)
                    programs = 3
                else if (programDto?.P2_ID != null)
                    programs = 2
                else if (programDto?.P1_ID != null)
                    programs = 1

            } else {
                programDTO.C.toInt().let {
                    programs = it
                }
            }
        }
    }
}