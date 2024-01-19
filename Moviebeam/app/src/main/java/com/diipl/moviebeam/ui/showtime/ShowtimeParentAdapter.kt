package com.diipl.moviebeam.ui.showtime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeGenre

class ShowtimeParentAdapter(
    private var onItemClicked: (Detail,Int) -> Unit
) :
    RecyclerView.Adapter<ShowtimeParentAdapter.ParentViewHolder>() {

    private var showsList: List<String> = emptyList()
    private var genreMap: Map<String, List<Detail>> = emptyMap()

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.parentTitleTv)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.langRecyclerView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moviegenre_parent_item, parent, false)
        return ParentViewHolder(view)
    }
    override fun getItemCount(): Int {
        return showsList.size
    }
    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = showsList[position]
        holder.titleTv.text = parentItem

        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val adapter = ShowtimeChildAdapter(genreMap[parentItem] ?: emptyList(), onItemClicked)
        holder.childRecyclerView.adapter = adapter
    }
    fun setShowsList(map: Map<String, List<Detail>>) {
        showsList = map.keys.toList()
        genreMap = map
        notifyDataSetChanged()
    }
}