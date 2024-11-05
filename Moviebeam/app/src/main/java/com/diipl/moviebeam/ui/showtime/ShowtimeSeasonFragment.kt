package com.diipl.moviebeam.ui.showtime

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.showtime.Season
import com.diipl.moviebeam.data.dto.showtime.ShowTimeContent
import com.diipl.moviebeam.data.dto.showtime.ShowTimeResponse
import com.diipl.moviebeam.databinding.FragmentShowtimeSeasonBinding
import com.diipl.moviebeam.service.receiver.LoggingService
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.ui.movies.MoviesViewModel
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadImagesWithGlideExtSushi
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class ShowtimeSeasonFragment : BaseFragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentShowtimeSeasonBinding? = null
    val binding get() = _binding!!
    private lateinit var adapter: ShowtimeSeasonChildAdapter
    private var position: Int = 0

    private val showtimeViewModel: ShowtimeViewModel by activityViewModels()
    private val viewModel: MoviesViewModel by activityViewModels()
    private var selectedShow: ShowTimeContent? = null

    private var seasonList: List<Season> = listOf()
    private var isListUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("movieReleaseId")
        }
    }

    override fun observeViewModel() {
        observe(showtimeViewModel.showtimeLiveData, ::handleShowtimeServiceResponse)
    }

    override fun initViewBinding() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowtimeSeasonBinding.inflate(inflater, container, false)
        LoggingService.sendMessageToWebSocket("In ShowtimeDetailPage Season create ", "13")
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.btnSeasonList.setOnKeyListener { v, keyCode, event ->
            if (isListUpdated){
                when(keyCode){
                    KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_DOWN_RIGHT, KeyEvent.KEYCODE_DPAD_UP_RIGHT -> {
                        isListUpdated = false
                        binding.seasonListRecyclerView.getChildAt(0).requestFocus()
                    }
                }
            }
            false
        }

    }

    private fun handleShowtimeServiceResponse(status: Resource<ShowTimeResponse>) {
        when (status) {
            is Resource.Loading -> {
                binding.loaderView.toVisible()
            }

            is Resource.Success -> {
                val response = showtimeViewModel.showtimeLiveData.value?.data
                val detailsContent = response?.shoContentList?.find { showtimeContent ->
                    position == showtimeContent.releaseId
                }
                detailsContent?.let { setShowDetails(it) }
                val detail = response?.shoGenreList?.get(0)?.detailList?.find { detail ->
                    detail.releaseId == position
                }

                val httpStreamingHotelVideoUrl =
                    "http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
                val imgPath =
                    httpStreamingHotelVideoUrl + detail?.releaseId + "/" + detail?.releaseId + "_S.jpg"
                binding.ivSeasonMovieImage.loadImagesWithGlideExtSushi(imgPath)

                binding.loaderView.toInvisible()
                binding.btnSeasonList.requestFocus()
            }

            else -> {
                status.errorCode?.let { showtimeViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun setShowDetails(detail: ShowTimeContent) {
        selectedShow = detail
        binding.tvSeasonTitle.text = detail.movieName
        binding.tvSeasonDirectorTitle.text = "Director : " + detail.director;
        binding.seasonListRecyclerView.setHasFixedSize(true)
        seasonList = detail.seasonList
        val seasonNames = seasonList.map { it.name }
        val dropdown: Spinner = binding.btnSeasonList
        dropdown.requestFocus()

        dropdown.handleFocusChange()
        val dropdownAdapter =
            ArrayAdapter(binding.root.context, R.layout.item_spinner_header, seasonNames)
        dropdownAdapter.setDropDownViewResource(R.layout.item_spinner_item)

        dropdown.adapter = dropdownAdapter
        dropdown.onItemSelectedListener = this

        dropdown.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        binding.seasonListRecyclerView.requestFocus()
                        return@setOnKeyListener true
                    }
                }
            }
            false
        }

        adapter = ShowtimeSeasonChildAdapter(
            onItemClicked = { movieDetail ->
                viewModel.insertShowDetails(movieDetail)
                viewModel.getShowData(movieDetail.releaseId)
                viewModel.seriesData.observe(this) {
                    (activity as ShowtimeActivity?)?.gotoExoPlayerActivity(
                        movieDetail, false, true,
                        it?.currentSeek ?: 0
                    )
                }

            },
            onLeftKeyPressed = {
                binding.btnSeasonList.post {
                    binding.btnSeasonList.requestFocus()
                }
            }
        )

        selectedShow?.let {
            adapter.updateSeasons(it.seasonList[0].detailList)
        }
        binding.seasonListRecyclerView.adapter = adapter
        binding.seasonListRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        binding.nestedScroll.isSmoothScrollingEnabled = true
        binding.nestedScroll.smoothScrollTo(0,0)
        adapter.updateSeasons(seasonList[position].detailList)
        isListUpdated = true
        adapter.notifyDataSetChanged()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}