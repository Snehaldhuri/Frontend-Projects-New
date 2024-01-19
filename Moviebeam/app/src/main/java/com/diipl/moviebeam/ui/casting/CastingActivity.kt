package com.diipl.moviebeam.ui.casting

import android.webkit.WebView
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.databinding.ActivityCastingBinding
import com.diipl.moviebeam.ui.base.BaseActivity


class CastingActivity : BaseActivity() {

    private lateinit var binding: ActivityCastingBinding

    override fun observeViewModel() {}

    override fun initViewBinding() {
        binding = ActivityCastingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val webView: WebView = binding.wvCasting
        webView.loadUrl(Constants.CASTING_URL)
    }

}