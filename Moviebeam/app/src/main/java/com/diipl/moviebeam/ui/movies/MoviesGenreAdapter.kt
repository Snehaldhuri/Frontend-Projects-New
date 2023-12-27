package com.diipl.moviebeam.ui.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.GenreDto

class MoviesGenreAdapter : RecyclerView.Adapter<MoviesGenreAdapter.MyViewHolder>() {

    private val genreList: MutableList<List<ContentDto>> = mutableListOf()
    private val genreListsMap: MutableMap<String, MutableList<ContentDto>> = mutableMapOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_movie_row)
        val textView: TextView = itemView.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_row, parent, false)
        view.findViewById<RecyclerView>(R.id.rv_movie_row)
            .layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = genreList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = genreList[position]
        holder.textView.text = item[0].genre1
        val cardAdapter = MoviesCardAdapter {

        }
        cardAdapter.setContentList(item)
        holder.recyclerView.adapter = cardAdapter
    }

    fun setGenreList(genres: List<GenreDto>, premiumContentList: List<ContentDto>) {
        for (genre in genres) {
            // Create an empty list for each genre
            val genreContentList: MutableList<ContentDto> = mutableListOf()
            genreListsMap[genre.genreName] = genreContentList
        }

        // Assuming you have access to the premium content list
        // Replace `premiumContentList` with your actual content list
        for (content in premiumContentList) {
            val genreName: String = content.genre1
            val genreContentList: MutableList<ContentDto>? = genreListsMap[genreName]
            genreContentList?.add(content)
        }

        // Populate the main genreList with the values from the map
        genreList.clear()
        genreList.addAll(genreListsMap.values)
        notifyDataSetChanged()
    }
}
