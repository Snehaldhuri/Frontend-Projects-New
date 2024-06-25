package com.diipl.moviebeam.ui.casting

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import com.diipl.moviebeam.databinding.ActivityCastingBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.service.LoggingService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getCurrentPanelNumber


class CastingActivity : BaseActivity() {

    private lateinit var binding: ActivityCastingBinding

    override fun observeViewModel() {}

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViewBinding() {
        try {
            binding = ActivityCastingBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val webView: WebView = binding.wvCasting
            webView.clearCache(true)
            webView.settings.javaScriptEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            Constants.CASTING_URL?.let { webView.loadUrl(it) }
            LoggingService.sendMessageToWebSocket(
                "In CastingPage activity",
                getCurrentPanelNumber()
            )
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket("${e.message}", getCurrentPanelNumber())
        }

    }

}