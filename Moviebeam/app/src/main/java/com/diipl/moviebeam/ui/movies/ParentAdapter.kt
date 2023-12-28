package com.diipl.moviebeam.ui.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto

class ParentAdapter(
    private var onItemClicked: (ContentDto) -> Unit
) :
    RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    private var movieList: MutableList<List<ContentDto>> = mutableListOf()

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.parentTitleTv)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.langRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parent_item, parent, false)
        return ParentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = movieList[position]
        holder.titleTv.text = parentItem[0].genre1

        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val adapter = ChildAdapter(parentItem, onItemClicked)
        holder.childRecyclerView.adapter = adapter
    }

    fun setMovieList(map: Map<String, List<ContentDto>>) {
        val list: MutableList<List<ContentDto>> = mutableListOf()
        map.keys.forEach {
            map[it]?.let { genre ->
                list.add(genre)
            }
        }
        movieList = list

    }


}