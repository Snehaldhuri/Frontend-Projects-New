package com.diipl.moviebeam.ui.guest.news

import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.news.NewsHeaderResponse
import com.diipl.moviebeam.data.dto.news.NewsResponse
import com.diipl.moviebeam.databinding.ActivityNewsBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.guestservice.news.NewsHeaderTabAdapter
import com.diipl.moviebeam.ui.guestservice.news.NewsTabAdapter
import com.diipl.moviebeam.ui.guestservice.news.NewsViewModel
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity() : BaseActivity() {

    private lateinit var binding: ActivityNewsBinding
    private val newsViewModel: NewsViewModel by viewModels()

    private var selectedHeaderItemPosition = 0
    private val selectedMenuItemPosition = 0

    private var newsHeaderPosition: Int = 0
    private var headerView: View? = null

    override fun observeViewModel() {
        observe(newsViewModel.newsHeaderLiveData, ::handleNewsHeaderResponse)
        observe(newsViewModel.newsLiveData, ::handleNewsDetailsResponse)
    }

    override fun initViewBinding() {
        binding = ActivityNewsBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        binding.btnBack.handleFocusChange()
        binding.btnBack.setOnClickListener { handleBackClick() }

        setContentView(binding.root)
    }

    private fun handleNewsHeaderResponse(status: Resource<NewsHeaderResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                val newsHeaderDetails = newsViewModel.newsHeaderLiveData.value?.data
                binding.rvNewsHeader.layoutManager = LinearLayoutManager(this)
                val adapter = NewsHeaderTabAdapter(
                    onMenuItemClicked = { it, view, pos ->
                        headerView = view
                        for ((index, item) in newsViewModel.newsHeaderLiveData.value?.data?.newsHeaderList?.withIndex()!!) {
                            if (item.id == it.id) {
                                selectedHeaderItemPosition = index
                                break
                            }
                        }
                        newsViewModel.fetchNewsDetails(it.id)
                        newsHeaderPosition = pos
                    },
                    onRightKeyPressed = {
                        binding.rvNews.smoothScrollToPosition(selectedMenuItemPosition)
                        binding.rvNews.findViewHolderForAdapterPosition(selectedMenuItemPosition)?.itemView?.requestFocus()
                    }, onLeftKeyPressed = {
                        it.setOnKeyListener { _, i, _ ->
                            when (i) {
//                                KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
                            }
                            false
                        }
                    }
                )
                newsHeaderDetails?.newsHeaderList?.let {
                    adapter.setNewsHeaderList(it)
                }
                binding.rvNewsHeader.adapter = adapter
                newsHeaderDetails?.newsHeaderList?.get(0)?.id?.let {
                    newsViewModel.fetchNewsDetails(it)
                }
                binding.rvNewsHeader.post {
                    binding.rvNewsHeader.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
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
                binding.rvNews.layoutManager = LinearLayoutManager(this)
                val adapter = NewsTabAdapter(onMenuItemFocused = {
                    binding.tvDescription.text = it.description
                    binding.tvPublishDate.text = it.publishDate
                }, onLeftKeyPressed = {
                    headerView?.let {
                        binding.rvNewsHeader.post {
                            binding.rvNewsHeader.findContainingItemView(it)?.requestFocus()
                        }
                    }
                })
                newsDetails?.newsList?.let {
                    adapter.setNewsList(it)
                }
                binding.rvNews.adapter = adapter
                newsDetails?.newsList?.get(0)?.let {
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

    override fun onBackPressed() {
        // Handle back press if needed
        super.onBackPressed()
    }

    fun handleBackClick() {
        finish()
    }
}
