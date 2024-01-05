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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.showtime.Season
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.databinding.FragmentShowtimeSeasonBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.loadImagesWithGlideExt
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toVisible

class ShowtimeSeasonFragment : BaseFragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentShowtimeSeasonBinding? = null
    val binding get() = _binding!!
    private var adapter: ShowtimeSeasonChildAdapter? = null
    private var position: Int = 0

    private val showtimeViewModel: ShowtimeViewModel by activityViewModels()

    private var selectedShow: ShowTimeContent? = null
    private var gradient: GradientDrawable? = null

    private var seasonList: List<Season> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("movieReleaseId")
        }
    }
    override fun observeViewModel() {
        observe(showtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
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
            is Resource.Loading -> { binding.loaderView.toVisible() }
            is Resource.Success -> {
                val response = showtimeViewModel.showtimeLiveData.value?.data
                val detailsContent = response?.shoContentList?.find { showtimeContent ->
                    position == showtimeContent.releaseId
                }
                detailsContent?.let { setShowDetails(it)  }
                val detail = response?.shoGenreList?.get(0)?.detailList?.find { detail ->
                    detail.releaseId == position
                }
                detail?.secImagePathSushi?.let {
                    setImage(it)
                }
            }
            else -> {
                status.errorCode?.let { showtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }
    private fun setImage(imgPath: String){
        binding.ivSeasonMovieImage.loadImagesWithGlideExt(imgPath)
    }
    fun setShowDetails(detail: ShowTimeContent) {
        selectedShow = detail
        binding.tvSeasonTitle.text = detail.movieName
        binding.tvSeasonDirectorTitle.text = detail.director
        binding.btnSeasonList.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = gradient
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.seasonListRecyclerView.setHasFixedSize(true)
        seasonList = detail.seasonList
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
        binding.seasonListRecyclerView.adapter = adapter
        binding.seasonListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        adapter?.updateSeasons(seasonList[position].detailList)
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}