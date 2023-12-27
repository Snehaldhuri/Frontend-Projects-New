package com.diipl.moviebeam.ui.movies

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.dto.datetime.DateTimeResponse
import com.diipl.moviebeam.data.dto.movies.ContentDto
import com.diipl.moviebeam.data.dto.movies.MoviesResponse
import com.diipl.moviebeam.data.dto.movies.PremiumGenre
import com.diipl.moviebeam.data.dto.theme.ThemeResponse
import com.diipl.moviebeam.data.dto.weather.WeatherResponse
import com.diipl.moviebeam.databinding.ActivityMoviesBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesActivity : BaseActivity()  {

    private lateinit var binding: ActivityMoviesBinding

    private var gradientStartColor = "#85bf08"
    private var gradientEndColor = "#0ca654"

    private val list: List<BtnModel> = Constants.MOVIES_PAGE_MENU_BUTTON_LIST

    private val MoviesViewModel: MoviesViewModel by viewModels()

    private val parentList = ArrayList<ParentItem>()
    override fun observeViewModel() {
        observe(MoviesViewModel.weatherLiveData, ::handleWeatherResponse)
        observe(MoviesViewModel.themeLiveData, ::handleThemeResponse)
        observe(MoviesViewModel.dateTimeLiveData, ::handleDateTimeResponse)
        observe(MoviesViewModel.moviesLiveData, ::handleMoviesServiceResponse)
    }

    override fun initViewBinding() {
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnFocusChangeListener { view, b ->
            if (b) {
                binding.btnBack.background = getGradient(gradientStartColor, gradientEndColor)
            } else {
                binding.btnBack.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }

        val parentRecyclerView: RecyclerView = binding.parentRecyclerView
        parentRecyclerView.setHasFixedSize(true)
        binding.parentRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

//        addDataToList()
//        val listAdapter = ParentAdapter()
//        binding.parentRecyclerView.adapter = listAdapter

        val cardRecyclerView: RecyclerView = binding.menuRecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    private fun addDataToList(){
        val childItems1= ArrayList<ChildItem>()
        childItems1.add(ChildItem("rt", R.drawable.casting_icon))
        childItems1.add(ChildItem("ferd", R.drawable.logo_static))
        childItems1.add(ChildItem("fg", R.drawable.adult))
        childItems1.add(ChildItem("rdsght", R.drawable.adult_day_pass_black))
        childItems1.add(ChildItem("fdgsdf", R.drawable.casting_icon))

       parentList.add(ParentItem("Game Development" , R.drawable.adult , childItems1))

        val childItems2= ArrayList<ChildItem>()
        childItems2.add(ChildItem("rt", R.drawable.casting_icon))
        childItems2.add(ChildItem("ferd", R.drawable.logo_static))
        childItems2.add(ChildItem("fg", R.drawable.adult))
        childItems2.add(ChildItem("rdsght", R.drawable.adult_day_pass_black))
        childItems2.add(ChildItem("fdgsdf", R.drawable.casting_icon))

        parentList.add(ParentItem("Android" , R.drawable.adult , childItems2))

        val childItems3= ArrayList<ChildItem>()
        childItems3.add(ChildItem("rt", R.drawable.casting_icon))
        childItems3.add(ChildItem("ferd", R.drawable.logo_static))
        childItems3.add(ChildItem("fg", R.drawable.adult))
        childItems3.add(ChildItem("rdsght", R.drawable.adult_day_pass_black))
        childItems3.add(ChildItem("fdgsdf", R.drawable.casting_icon))

        parentList.add(ParentItem("c Development" , R.drawable.adult , childItems3))

    }
    private fun handleMoviesServiceResponse(status: Resource<MoviesResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                val response = MoviesViewModel.moviesLiveData.value?.data

//                Log.d("TAG11", "handleMoviesServiceResponse: ${genreMap.keys}")

                Glide.with(this)
                    .load(MoviesViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.layoutHeader.ivHotelLogo)
                loadBg(MoviesViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                val adapter = MoviesBtnAdapter(list){ btnId ->
                    when (btnId) {

                        Constants.MOVIE_RENTALS_ID -> {
                            val genreMap: HashMap<String, MutableList<ContentDto>> = HashMap()
                            response?.premiumContentList?.forEach{
                                if(genreMap[it.genre1] != null){
                                    genreMap[it.genre1]?.add(it)
                                }else{
                                        val movieList = mutableListOf<ContentDto>()
                                        movieList.add(it)
                                        genreMap[it.genre1] = movieList
                                }
                            }
                            val parentAdapter =ParentAdapter()
                            parentAdapter.setMovieList(genreMap)
                            binding.parentRecyclerView.adapter = parentAdapter
//                            val adapter = ParentAdapter(parentList)
//                            binding.parentRecyclerView.adapter = adapter

//                            val premiumContentList = response?.premiumContentList ?: response?.premiumContentList
//                            binding.rvRentalMovies.layoutManager =
//                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//                            val genreAdapter = MoviesGenreAdapter()
////                            genreAdapter.setGenreList(response?.premiumGenreList ?: emptyList(), response?.premiumContentList ?: emptyList())
//                            genreAdapter.setGenreList((response?.premiumGenreList ?: emptyList()) as List<GenreDto>, response?.premiumContentList ?: emptyList())
//                            binding.rvRentalMovies.adapter = genreAdapter
//                            val cardAdapter = MoviesCardAdapter {
//
//                            }
//                            cardAdapter.setContentList(response?.premiumContentList ?: emptyList())
//
//                            binding.parentRecyclerView.adapter = cardAdapter

                        }
                        Constants.FREE_MOVIES_ID -> {
                            val genreMap: HashMap<String, MutableList<ContentDto>> = HashMap()
                            response?.freeContentList?.forEach{
                                if(genreMap[it.genre1] != null){
                                    genreMap[it.genre1]?.add(it)
                                }else{
                                    val movieList = mutableListOf<ContentDto>()
                                    movieList.add(it)
                                    genreMap[it.genre1] = movieList
                                }
                            }
                            val parentAdapter =ParentAdapter()
                            parentAdapter.setMovieList(genreMap)
                            binding.parentRecyclerView.adapter = parentAdapter
//                            val cardAdapter = MoviesCardAdapter {
//
//                            }
//                            cardAdapter.setContentList(response?.freeContentList ?: emptyList())
//
//                            binding.recyclerView.adapter = cardAdapter
                        }
                        Constants.ADULT_DAY_PASS_ID -> {
//                            val cardAdapter = MoviesCardAdapter {
//
//                            }
//                            val adultDayList = response?.premiumContentList?.filter{ it.genre1 == "Adult Daypass" }
//                            cardAdapter.setContentList(adultDayList ?: emptyList())
//                            binding.recyclerView.adapter = cardAdapter
                        }
                        Constants.ADULT_ID -> {
                            val genreMap: HashMap<String, MutableList<ContentDto>> = HashMap()
                            response?.premiumContentList?.forEach{
                                if(genreMap[it.genre1] != null){
                                    genreMap[it.genre1]?.add(it)
                                }else{
                                    val movieList = mutableListOf<ContentDto>()
                                    movieList.add(it)
                                    genreMap[it.genre1] = movieList
                                }
                            }
                            val parentAdapter =ParentAdapter()
                            parentAdapter.setMovieList(genreMap)
                            binding.parentRecyclerView.adapter = parentAdapter
//                            val cardAdapter = MoviesCardAdapter {
//
//                            }
//                            val adultList = response?.premiumContentList?.filter{ it.genre1 == "Adult" }
//                            cardAdapter.setContentList(adultList ?: emptyList())
//                            binding.recyclerView.adapter = cardAdapter
                        }
                        else -> {

                        }
                    }
                }
//                val listAdapter = ParentAdapter(parentList)
//                binding.parentRecyclerView.adapter = listAdapter
//                val cardAdapter = MoviesCardAdapter {
//
//                }
//                cardAdapter.setContentList(response?.premiumContentList ?: emptyList())
                val genreMap: HashMap<String, MutableList<ContentDto>> = HashMap()
                response?.premiumContentList?.forEach{
                    if(genreMap[it.genre1] != null){
                        genreMap[it.genre1]?.add(it)
                    }else{
                        val movieList = mutableListOf<ContentDto>()
                        movieList.add(it)
                        genreMap[it.genre1] = movieList
                    }
                }
                val parentAdapter =ParentAdapter()
                parentAdapter.setMovieList(genreMap)
                binding.parentRecyclerView.adapter = parentAdapter

                adapter.setGradientColor(gradientStartColor, gradientEndColor)
                binding.menuRecyclerView.adapter = adapter

                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }
    private fun handleWeatherResponse(status: Resource<WeatherResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                var temperature = MoviesViewModel.weatherLiveData.value?.data?.tempCondition
                temperature?.let {
                    if (it.contains("&deg C")) {
                        temperature = it.replace("&deg C", " \u2103")
                    } else {
                        temperature = it.replace("&deg F", " \u2109")
                    }
                }
                binding.layoutHeader.layoutWeatherTime.layoutWeather.txtTemperature.text = temperature
                Glide.with(this)
                    .load(MoviesViewModel.weatherLiveData.value?.data?.tempConditionUrlCloud)
                    .into(binding.layoutHeader.layoutWeatherTime.layoutWeather.ivWeather)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleThemeResponse(status: Resource<ThemeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                MoviesViewModel.themeLiveData.value?.data?.gradientColor?.let {
                    gradientStartColor = it
                }
                MoviesViewModel.themeLiveData.value?.data?.spotLightColor?.let {
                    gradientEndColor = it
                }
                Glide.with(this)
                    .load(MoviesViewModel.themeLiveData.value?.data?.themeLogoFileName)
                    .into(binding.layoutHeader.ivHotelLogo)
                loadBg(MoviesViewModel.themeLiveData.value?.data?.themeBackgroundFileName)
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleDateTimeResponse(status: Resource<DateTimeResponse>) {
        when (status) {
            is Resource.Loading -> binding.loaderView.toVisible()
            is Resource.Success -> {
                binding.layoutHeader.layoutWeatherTime.tvDate.text = MoviesViewModel.dateTimeLiveData.value?.data?.date
                binding.layoutHeader.layoutWeatherTime.tvTime.text = MoviesViewModel.dateTimeLiveData.value?.data?.time
                binding.loaderView.toInvisible()
            }

            else -> {
                status.errorCode?.let { MoviesViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun loadBg(imgUrl: String?) {
        Glide.with(this).load(imgUrl)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    resource.alpha = 120
                    binding.root.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
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
}