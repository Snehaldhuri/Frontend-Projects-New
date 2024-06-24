package com.diipl.moviebeam.ui.programguide

import android.content.Context
import android.graphics.Color
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO
import com.diipl.moviebeam.databinding.ProgramCardBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import kotlin.math.roundToInt

class ProgramAdapter(
    private val onProgramFocused: (program: ChannelEpgDTO, title: String?, synopsis: String?) -> Unit,
    private val onProgramClicked: (program: ChannelEpgDTO) -> Unit,
    private val loadPreviousPrograms: () -> Unit,
    private val loadNextPrograms: () -> Unit
) : RecyclerView.Adapter<ProgramAdapter.MyViewHolder>() {

    private var programDto: ChannelEpgDTO? = null
    private var programs: Int = 0
    private var p4Dst: String? = null

    inner class MyViewHolder(val binding: ProgramCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ProgramCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true

        val params = binding.root.layoutParams
//        params.width = getWidthInPercent(parent.context, 14)
        params.height = getHeightInPercent(parent.context, 4)

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
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P1_PT, programDto?.P1_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            1 -> {
                setProgramTitle(holder.binding.programName, programDto?.P2_PT)
                setProgramWidth(holder.binding.root, programDto?.P2_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P2_PT, programDto?.P2_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            2 -> {
                setProgramTitle(holder.binding.programName, programDto?.P3_PT)
                setProgramWidth(holder.binding.root, programDto?.P3_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P3_PT, programDto?.P3_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            3 -> {
                setProgramTitle(holder.binding.programName, programDto?.P4_PT)
                setProgramWidth(holder.binding.root, programDto?.P4_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P4_PT, programDto?.P4_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            4 -> {
                setProgramTitle(holder.binding.programName, programDto?.P5_PT)
                setProgramWidth(holder.binding.root, programDto?.P5_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P5_PT, programDto?.P5_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            5 -> {
                setProgramTitle(holder.binding.programName, programDto?.P6_PT)
                setProgramWidth(holder.binding.root, programDto?.P6_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P6_PT, programDto?.P6_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            6 -> {
                setProgramTitle(holder.binding.programName, programDto?.P7_PT)
                setProgramWidth(holder.binding.root, programDto?.P7_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P7_PT, programDto?.P7_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }

            7 -> {
                setProgramTitle(holder.binding.programName, programDto?.P8_PT)
                setProgramWidth(holder.binding.root, programDto?.P8_CLS)
                holder.binding.root.setOnFocusChangeListener { view, isFocused ->
                    if (isFocused) {
                        programDto?.let {
                            onProgramFocused(it, programDto?.P8_PT, programDto?.P8_SY)
                        }
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_YELLOW))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_BLACK))
                    } else {
                        view.setBackgroundColor(Color.parseColor(Constants.COLOR_BLACK))
                        holder.binding.programName.setTextColor(Color.parseColor(Constants.COLOR_WHITE))
                    }
                }
            }
        }

        if (position == programs - 1) {
            holder.binding.root.setOnKeyListener { _, keycode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keycode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            loadNextPrograms()
                        }
                    }
                }
                false
            }
        } else if (position == 0) {
            holder.binding.root.setOnKeyListener { _, keycode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keycode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            loadPreviousPrograms()
                        }
                    }
                }
                false
            }
        }
    }

    private fun setProgramWidth(view: View, percentStr: String?) {
        val percent = getWidthInPercent(view.context, percentStr?.toFloat() ?: 0F)
        val params = view.layoutParams
        params.width = percent
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

    fun setProgramDto(programDTO: ChannelEpgDTO?) {
        this.programDto = programDTO
        programs = programDTO?.C?.toInt() ?: 0
    }
}