package com.diipl.moviebeam.ui.showtime

import android.graphics.Color
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
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
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
    private var position: Int = 0

    private var gradient: GradientDrawable? = null

    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR

    private val showtimeViewModel: ShowtimeViewModel by activityViewModels()

    private var selectedShow: ShowTimeContent? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                val httpStreamingHotelvideoUrl ="http://d1l6t4e2m4gzwb.cloudfront.net/PosterImages/"
                detail?.imagePathSushi =httpStreamingHotelvideoUrl+detail?.releaseId+"/"+detail?.releaseId+"_S.jpg"

                detail?.imagePathSushi?.let {
                    setImage(it)
                }
                binding.loaderView.toInvisible()
                binding.btnSeasonList.requestFocus()
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
        binding.tvSeasonDirectorTitle.setText("Director : " + detail.director);
        binding.seasonListRecyclerView.setHasFixedSize(true)
        seasonList = detail.seasonList
        val seasonNames = seasonList.map { it.name }
        val dropdown: Spinner = binding.btnSeasonList
        dropdown.requestFocus()

        dropdown.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                dropdown.background = gradient
            } else {
                dropdown.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        val dropdownAdapter = ArrayAdapter(binding.root.context, R.layout.item_spinner_header, seasonNames)
        dropdownAdapter.setDropDownViewResource(R.layout.item_spinner_item)

        dropdown.adapter = dropdownAdapter
        dropdown.onItemSelectedListener = this

        adapter = ShowtimeSeasonChildAdapter(
            onItemClicked = { movieDetail ->
                (activity as ShowtimeActivity?)?.gotoExoPlayerActivity(movieDetail, false, true)
            },
            onLeftKeyPressed = {
                binding.btnSeasonList.postDelayed({
                    binding.btnSeasonList.requestFocus()
                }, 1)
            }
        )

        gradient?.let {
            adapter?.setGradient(it)
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
    private fun getGradient(startColor: String, endColor: String): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )

        gradientDrawable.cornerRadius = 20f

        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    fun setGradient(gradient: GradientDrawable) {
        this.gradient = gradient
    }

}