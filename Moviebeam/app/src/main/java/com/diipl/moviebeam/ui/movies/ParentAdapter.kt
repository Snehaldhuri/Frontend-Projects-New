package com.diipl.moviebeam.ui.movies

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.Constants.MOVIE_PARENT_POSITION
import com.diipl.moviebeam.Constants.MOVIE_SELECTED_POSITION
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.databinding.MoviegenreParentItemBinding
import com.diipl.moviebeam.utils.getHeightInPercent

private const val TAG = "ParentAdapter"

class ParentAdapter(
    private var onItemClicked: (ContentDto, View) -> Unit,
    private val onLeftKey: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

    private var movieList: MutableList<List<ContentDto>> = mutableListOf()

    private lateinit var rootBinding: MoviegenreParentItemBinding
    lateinit var viewHolder: ParentViewHolder


    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.parentTitleTv)
        val childRecyclerView: RecyclerView = itemView.findViewById(R.id.langRecyclerView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        rootBinding =
            MoviegenreParentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val params = rootBinding.root.layoutParams
//        params.width = getWidthInPercent(parent.context, 80)
        params.height = getHeightInPercent(parent.context, 40)

        return ParentViewHolder(rootBinding.root)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = movieList[position]
        holder.titleTv.text = parentItem[0].genre1
        viewHolder = holder

        holder.childRecyclerView.setHasFixedSize(true)
        holder.childRecyclerView.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        Log.e(TAG, "onBindViewHolder: ${holder.itemView.id}")

        val adapter = ChildAdapter(parentItem, onItemClicked = {it, view->
            MOVIE_PARENT_POSITION = holder.absoluteAdapterPosition
            onItemClicked(it, holder.itemView)
        }, onLeftKey = {
            onLeftKey(it)
        })
        holder.childRecyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        holder.childRecyclerView.adapter = adapter

    }

    fun setMovieList(
        map: Map<String, List<ContentDto>>?,
        mapList: MutableList<List<ContentDto>>?,
        isMap: Boolean
    ) {
        if (isMap) {
            val list: MutableList<List<ContentDto>> = mutableListOf()
            val keys = map?.keys?.toMutableList()
            keys?.remove("New Releases")
            keys?.add(0, "New Releases")
            keys?.remove("All Pay Movies")
            keys?.add(1, "All Pay Movies")
            keys?.forEach {
                map[it]?.let { genre ->
                    list.add(genre)
                }
            }
            movieList = list
        } else {
            if (mapList != null) {
                movieList = mapList
            }
        }

    }

    fun updateFocus() {
        Log.e(TAG, "updateFocus 0 : $MOVIE_PARENT_POSITION")
        if (::rootBinding.isInitialized && ::viewHolder.isInitialized) {
            rootBinding.langRecyclerView.post {
                Log.e(TAG, "updateFocus 1 : ${viewHolder.absoluteAdapterPosition}")
//                if (viewHolder.absoluteAdapterPosition == MOVIE_PARENT_POSITION)
                    rootBinding.langRecyclerView.findViewHolderForAdapterPosition(
                        MOVIE_SELECTED_POSITION
                    )?.itemView?.requestFocus()
//                rootBinding.langRecyclerView.findContainingItemView(selectedView)?.requestFocus()


                MOVIE_PARENT_POSITION = -1
                MOVIE_SELECTED_POSITION = -1
            }
            /*val adapter = rootBinding.langRecyclerView.adapter as ChildAdapter
            adapter.updateFocus()*/
        }
    }


}