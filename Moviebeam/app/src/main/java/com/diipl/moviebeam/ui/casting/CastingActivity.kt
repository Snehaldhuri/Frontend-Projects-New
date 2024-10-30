package com.diipl.moviebeam.ui.casting

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.databinding.ActivityCastingBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.logE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CastingActivity : BaseActivity() {

    private lateinit var binding: ActivityCastingBinding

    override fun observeViewModel() {}

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViewBinding() {
        binding = ActivityCastingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val webView: WebView = binding.wvCasting
                webView.clearCache(true)
                webView.settings.javaScriptEnabled = true
                webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                webView.loadUrl(preferenceHandler.castingUrl)
            } catch (e: Exception) {
                logE("${e.message}")
            }
        }
    }


    fun handleBackClick() {
        finish()
    }

}