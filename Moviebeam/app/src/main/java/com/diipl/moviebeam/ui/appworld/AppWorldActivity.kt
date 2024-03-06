package com.diipl.moviebeam.ui.appworld

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.SelectedApps
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityAppWorldBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getCurrentPanelNumber
import com.diipl.moviebeam.utils.loadImagesWithGlideExtLogo
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Collections
import javax.inject.Inject

@AndroidEntryPoint
class AppWorldActivity : BaseActivity() {

    private lateinit var binding: ActivityAppWorldBinding
    private val appWorldViewModel: AppWorldViewModel by viewModels()

    private var gradient: GradientDrawable? = null
    private var isCheckedIn = false
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper


    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    override fun observeViewModel() {
        observe(appWorldViewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(appWorldViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
    }

    override fun initViewBinding() {
        binding = ActivityAppWorldBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            appWorldViewModel.getAccountSetupResponseData(accountSetupDataStore)
            preferenceDataStoreHelper = PreferenceDataStoreHelper(this)

            appWorldViewModel.validateSession(preferenceDataStoreHelper)
            fetchDetails()
            binding.rvApps.layoutManager = GridLayoutManager(this, 4)
            binding.btnBack.setOnClickListener { finish() }
            binding.btnBack.setOnFocusChangeListener { view, isFocused ->
                if (isFocused) {
                    view.background = gradient
                } else {
                    view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
                }
            }
            Log.d("checked in ", "checked in $isCheckedIn")
            LoggingService.sendMessageToWebSocket(
                "In AppWorldMain activity",
                getCurrentPanelNumber()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoggingService.sendMessageToWebSocket(
                "launchApp Exception in AppWorldMain activity ${e.message}",
                getCurrentPanelNumber()
            )
        }
    }

    private fun handleValidateSessionResponse(status: Boolean) {
        this.isCheckedIn = status
    }

    private fun getInstalledApps(apiAppList: List<SelectedApps>) {
        try {
            val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val installedApps = filterSystemApps(allApps)
            val selectedApps = mutableListOf<ApplicationInfo>()
            apiAppList.forEach { selectedApp ->
                installedApps.forEach { installedApp ->
                    if (selectedApp.forAndroid and (selectedApp.value == installedApp.packageName))
                        selectedApps.add(installedApp)
                }
            }
            val adapter = AppAdapter {
                if (packageManager.getLaunchIntentForPackage(it.packageName) == null) {
                    launchAppSecured(it.packageName)
                } else {
                    launchApp(it.packageName)
                }
            }
            adapter.setAppList(selectedApps)
            binding.rvApps.adapter = adapter
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "getInstalledApps Exception in AppWorldMain activity ${e.message}",
                getCurrentPanelNumber()
            )
        }
    }

    private fun createRequestBody(roomNo: String, UA: String, accessType: Int): String {
        val netflixDetails = JSONObject().apply {
            put("stbRoomNo", roomNo)
            put("ua", UA)
            put("accessType", accessType)
        }
        return netflixDetails.toString()
    }

    private fun postRequest(url: String, requestBody: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                LoggingService.sendMessageToWebSocket("Network error: ${e.message}", "09")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("sessionid url success", "sessionid url success")
                } else {
                    val responseBody = response.body?.string() ?: "No response body"
                    val responseCode = response.code
                    Log.e(
                        "sessionid error",
                        "sessionid Failed to call URL. Response code: $responseCode, Response body: $responseBody"
                    )
                }
            }
        })
    }

    private fun launchApp(packageName: String) {
        try {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))

            if (packageName == "com.netflix.ninja") {
                val sessionId = Constants.SESSION_ID
                val url =
                    "https://stb.moviebeam.com:1930/LG/rest/content/netflixAccess/enter?sessionId=$sessionId"

                val requestBody = createRequestBody(Constants.STB_ROOM_NO, Constants.UA, 1)

                postRequest(url, requestBody)
                Constants.NETFLIX_LAUNCHED = true;
            }

        } catch (e: Exception) {
            e.printStackTrace()
            LoggingService.sendMessageToWebSocket(
                "launchApp Exception in AppWorldMain activity ${e.message}",
                getCurrentPanelNumber()
            )
        }
    }


    private fun launchAppSecured(packageName: String?) {
        try {
            val intent = Intent()
            intent.setPackage(packageName)
            val pm = packageManager
            val resolveInfos = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            Collections.sort(resolveInfos, ResolveInfo.DisplayNameComparator(pm))
            if (resolveInfos.size > 0) {
                val launchable = resolveInfos[0]
                val activity = launchable.activityInfo
                val name = ComponentName(
                    activity.applicationInfo.packageName,
                    activity.name
                )
                val i = Intent(Intent.ACTION_MAIN)
                i.component = name
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                startActivity(i)
            }
        } catch (e: Exception) {
            LoggingService.sendMessageToWebSocket(
                "launchAppSecured Exception in AppWorldMain activity ${e.message}",
                getCurrentPanelNumber()
            )
        }
    }


    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                val response = appWorldViewModel.accountSetupLiveData.value?.data
                response?.selectedAppsList?.let {
                    getInstalledApps(it)
                }
                binding.pbLoader.toInvisible()
            }

            else -> {
                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
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

    private fun getGradient(startColor: String?, endColor: String?): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun filterSystemApps(apps: List<ApplicationInfo>): List<ApplicationInfo> {
        return apps.filter {
            !isSystemApp(it)
        }
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }


    private fun fetchDetails() {
        binding.layoutHeader.tvTitle.text = intent.extras?.getString("title")
        gradient = getGradient(
            intent.extras?.getString("gradientStartColor"),
            intent.extras?.getString("gradientEndColor")
        )
        intent.extras?.getString("themeLogoFileName")?.let {
            binding.layoutHeader.ivHotelLogo.loadImagesWithGlideExtLogo(it)
        }
        loadBg(intent.extras?.getString("themeBackgroundFileName"))
    }

}