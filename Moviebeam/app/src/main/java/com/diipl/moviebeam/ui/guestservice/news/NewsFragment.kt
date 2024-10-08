package com.diipl.moviebeam.ui.guestservice.news

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.news.NewsHeaderResponse
import com.diipl.moviebeam.data.dto.news.NewsResponse
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.FragmentNewsBinding
import com.diipl.moviebeam.ui.base.BaseFragment
import com.diipl.moviebeam.utils.getCurrentDateTime
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import kotlinx.coroutines.launch

class NewsFragment(private val onLeftKeyPressed: () -> Unit) : BaseFragment() {

    private var _binding: FragmentNewsBinding? = null
    val binding get() = _binding!!
    private val newsViewModel: NewsViewModel by activityViewModels()

    private var selectedHeaderItemPosition = 0
    private val selectedMenuItemPosition = 0

    private var newsHeaderPosition: Int = 0
    private var headerView: View? = null
    private val preferenceDataStoreHelper: PreferenceDataStoreHelper by lazy {
        PreferenceDataStoreHelper(this.requireContext())
    }
    private var ua = ""

    override fun observeViewModel() {
        observe(newsViewModel.newsHeaderLiveData, ::handleNewsHeaderResponse)
        observe(newsViewModel.newsLiveData, ::handleNewsDetailsResponse)
    }

    override fun initViewBinding() {
        lifecycleScope.launch {
            ua = getUa()
        }
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    private fun handleNewsHeaderResponse(status: Resource<NewsHeaderResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {

                val newsHeaderDetails = newsViewModel.newsHeaderLiveData.value?.data
                binding.rvNewsHeader.layoutManager = LinearLayoutManager(context)
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
                                KeyEvent.KEYCODE_DPAD_LEFT -> onLeftKeyPressed()
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
                binding.rvNews.layoutManager = LinearLayoutManager(context)
                val adapter = NewsTabAdapter(onMenuItemFocused = {
                    binding.tvTitle.text = it.title
                    binding.tvDescription.text = it.description
                    binding.tvPublishDate.text = it.publishDate
                }, onLeftKeyPressed = {
                    headerView?.let {
                        binding.rvNewsHeader.post {
                            binding.rvNewsHeader.findContainingItemView(it)?.requestFocus()
                        }
                    }
                })
                if (newsDetails != null) {
                    if (newsDetails.newsList.isNotEmpty()){
                        newsDetails.newsList.let {
                            adapter.setNewsList(it)
                        }
                        newsDetails.newsList[0].let {
                            binding.tvDescription.text = it.description
                            binding.tvPublishDate.text = it.publishDate
                        }
                    } else {
                        adapter.setNewsList(emptyList())
                        binding.tvDescription.text = "No news found!\n\n"
                        binding.tvPublishDate.text = getCurrentDateTime()
                    }
                    binding.rvNews.adapter = adapter
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

}