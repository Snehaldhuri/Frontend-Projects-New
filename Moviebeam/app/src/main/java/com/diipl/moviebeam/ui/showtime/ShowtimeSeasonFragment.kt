package com.diipl.moviebeam.ui.showtime

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.showtime.Detail
import com.diipl.moviebeam.data.dto.showtime.Season
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.databinding.FragmentShowtimeSeasonBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class ShowtimeSeasonFragment : BaseFragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentShowtimeSeasonBinding? = null
    val binding get() = _binding!!
    private var adapter: ShowtimeSeasonChildAdapter? = null
    private var showsList: List<String> = emptyList()
    private var detailMap: Map<String, List<Season>> = emptyMap()
    private var position: Int = 0

    private val ShowtimeViewModel: ShowtimeViewModel by activityViewModels()

    private var selectedShow: ShowTimeContent? = null
    private var gradient: GradientDrawable? = null

    private var seasonList: List<Season> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //value is passing from bundle but is not getting fetch in here
        arguments?.let {
            position = it.getInt("movieReleaseId")

        }
    }

    override fun observeViewModel() {
        observe(ShowtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
    }

    override fun initViewBinding() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowtimeSeasonBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                val response = ShowtimeViewModel.showtimeLiveData.value?.data
                Log.d("showNUMber", "handleShowtimeServiceResponse: $position")
                val detailsGenre = response?.shoContentList?.find { showtimeContent ->
                    position == showtimeContent.releaseId
                }
                Log.d("showContentList", "handleShowtimeServiceResponse: $detailsGenre")
            }

            else -> {
                status.errorCode?.let { ShowtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }


    fun setShowDetails(detail: ShowTimeContent) {
        selectedShow = detail
        binding.tvSeasonTitle.text = detail.movieName
        binding.tvSeasonDirectorTitle.text = detail.director
        binding.ivSeasonMovieImage.loadImagesWithGlideExt(detail.secImagePathSushi)

        binding.btnSeasonList.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.seasonListRecyclerView.setHasFixedSize(true)
        val seasonNames = seasonList.map { it.name }

        val dropdown: Spinner = binding.btnSeasonList

        val dropdownAdapter = ArrayAdapter(binding.root.context, R.layout.item_spinner_header, seasonNames)
        dropdownAdapter.setDropDownViewResource(R.layout.item_spinner_item)

        dropdown.adapter = dropdownAdapter
        dropdown.onItemSelectedListener = this
        dropdown.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.findViewById<TextView>(R.id.tv_title).isSelected = true
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                view.findViewById<TextView>(R.id.tv_title).isSelected = false
            }
        }

        adapter = ShowtimeSeasonChildAdapter() { season ->
        }
        selectedShow?.let {
            adapter!!.updateSeasons(it.seasonList[0].detailList)
        }

//        Log.d("ChildAdapter", "Data Size: ${detail.seasonList[0].detailList.elementAt(1)}")
        binding.seasonListRecyclerView.adapter = adapter
        binding.seasonListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    fun setAirportList(seasonList: List<Season>) {
        this.seasonList = seasonList
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        adapter?.updateSeasons(seasonList[position].detailList)
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}