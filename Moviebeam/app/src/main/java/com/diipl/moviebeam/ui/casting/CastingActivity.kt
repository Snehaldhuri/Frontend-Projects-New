package com.diipl.moviebeam.ui.casting

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityCastingBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CastingActivity : BaseActivity() {

    private lateinit var binding: ActivityCastingBinding

    //Variables from datastore
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var castingUrl = ""

    override fun observeViewModel() {}

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViewBinding() {
        try {
            binding = ActivityCastingBinding.inflate(layoutInflater)
            setContentView(binding.root)
            preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
            this.initializeDatastoreParams()
            val webView: WebView = binding.wvCasting
            webView.clearCache(true)
            webView.settings.javaScriptEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webView.loadUrl(castingUrl)
        } catch (e: Exception) {
            logE("${e.message}")
        }
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            castingUrl = getCastingUrl()
        }
    }

    private suspend fun getCastingUrl(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.CASTING_URL_KEY,
            ""
        )
    }

    fun handleBackClick() {
        finish()
    }

}