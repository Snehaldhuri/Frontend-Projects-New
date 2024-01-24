package com.diipl.moviebeam.ui.guestservice.news

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.news.NewsHeaderResponse
import com.diipl.moviebeam.data.dto.news.NewsResponse
import com.diipl.moviebeam.databinding.FragmentNewsBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible

class NewsFragment(private val onLeftKeyPressed: () -> Unit) : BaseFragment() {

    private var _binding: FragmentNewsBinding? = null
    val binding get() = _binding!!
    private val newsViewModel: NewsViewModel by activityViewModels()

    private var gradientStartColor: String? = null
    private var gradientEndColor: String? = null

    private var selectedHeaderItemPosition = 0
    private val selectedMenuItemPosition = 0

    private var newsHeaderPosition: Int = 0

    override fun observeViewModel() {
        observe(newsViewModel.newsHeaderLiveData, ::handleNewsHeaderResponse)
        observe(newsViewModel.newsLiveData, ::handleNewsDetailsResponse)
    }

    override fun initViewBinding() {

    }

    private fun handleNewsHeaderResponse(status: Resource<NewsHeaderResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                val newsHeaderDetails = newsViewModel.newsHeaderLiveData.value?.data
                binding.rvNewsHeader.layoutManager = LinearLayoutManager(context)
                val adapter = NewsHeaderTabAdapter(
                    onMenuItemClicked = { it, view, pos ->
                        for ((index, item) in newsViewModel.newsHeaderLiveData.value?.data?.newsHeaderList?.withIndex()!!) {
                            if (item.id == it.id) {
                                selectedHeaderItemPosition = index;
                                break;
                            }
                        }
                        newsViewModel.fetchNewsDetails(it.id)
                        newsHeaderPosition = pos
                    },
                    onRightKeyPressed = {
                        binding.rvNews.smoothScrollToPosition(selectedMenuItemPosition);
                        binding.rvNews.findViewHolderForAdapterPosition(selectedMenuItemPosition)?.itemView?.requestFocus();
                    }, onLeftKeyPressed = {
                        onLeftKeyPressed()

                    }
                )
                newsHeaderDetails?.newsHeaderList?.let {
                    adapter.setNewsHeaderList(it)
                }
                adapter.setGradient(getGradient())
                binding.rvNewsHeader.adapter = adapter
                newsHeaderDetails?.newsHeaderList?.get(0)?.id?.let {
                    newsViewModel.fetchNewsDetails(it)
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { newsViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun handleNewsDetailsResponse(status: Resource<NewsResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val newsDetails = newsViewModel.newsLiveData.value?.data
                binding.rvNews.layoutManager = LinearLayoutManager(context)
                val adapter = NewsTabAdapter(onMenuItemFocused = {
                    binding.tvTitle.text = it.title
                    binding.tvDescription.text = it.description
                    binding.tvPublishDate.text = it.publishDate
                }, onLeftKeyPressed = {
                    binding.rvNews.findViewHolderForAdapterPosition(selectedHeaderItemPosition)?.itemView?.requestFocus();
                })
                newsDetails?.newsList?.let {
                    adapter.setNewsList(it)
                }
                adapter.setGradient(getGradient())
                binding.rvNews.adapter = adapter
                newsDetails?.newsList?.get(0)?.let {
                    binding.tvTitle.text = it.title
                    binding.tvDescription.text = it.description
                    binding.tvPublishDate.text = it.publishDate
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { newsViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setGradientColor(startColor: String, endColor: String) {
        gradientStartColor = startColor
        gradientEndColor = endColor
    }

    private fun getGradient(
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 10f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.orientation = GradientDrawable.Orientation.TR_BL
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

}