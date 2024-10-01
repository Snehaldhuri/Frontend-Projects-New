package com.diipl.moviebeam.ui.appworld

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.Resource
import com.diipl.moviebeam.data.dto.accountsetup.AccountSetupResponse
import com.diipl.moviebeam.data.dto.accountsetup.SelectedApps
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivityAppWorldBinding
import com.diipl.moviebeam.databinding.PopupLayoutBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.ThemeDetails
import com.diipl.moviebeam.utils.clearCredentials
import com.diipl.moviebeam.utils.getGradientColor
import com.diipl.moviebeam.utils.handleFocusChange
import com.diipl.moviebeam.utils.loadBg
import com.diipl.moviebeam.utils.loadLogo
import com.diipl.moviebeam.utils.logD
import com.diipl.moviebeam.utils.logE
import com.diipl.moviebeam.utils.observe
import com.diipl.moviebeam.utils.toInvisible
import com.diipl.moviebeam.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    private var isCheckedIn = false

    //Variables from Datastore
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private var stbRoomNo = ""
    private var ua = ""
    private var appList = ArrayList<String>()

    @Inject
    lateinit var accountSetupDataStore: DataStore<AccountSetupResponse>

    override fun observeViewModel() {
        observe(appWorldViewModel.isGuestCheckedInLiveData, ::handleValidateSessionResponse)
        observe(appWorldViewModel.accountSetupLiveData, ::handleAccountSetupResponse)
    }

    override fun initViewBinding() {
        binding = ActivityAppWorldBinding.inflate(layoutInflater)
        binding.root.loadBg()
        binding.layoutHeader.ivHotelLogo.loadLogo()
        binding.layoutHeader.tvTitle.text = ThemeDetails.TITLE
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            appWorldViewModel.getAccountSetupResponseData(accountSetupDataStore)
            preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
            this.initializeDatastoreParams()

            appWorldViewModel.validateSession(preferenceDataStoreHelper)
            binding.rvApps.layoutManager = GridLayoutManager(this, 4)
            binding.btnBack.setOnClickListener { handleBackClick() }
            binding.btnBack.handleFocusChange()
            binding.btnClearCredentials.handleFocusChange()
            binding.btnClearCredentials.setOnClickListener {
                clearCredentials(appList)
                showPopup()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logE("launchApp Exception in App World activity ${e.message}")
        }
    }

    private fun handleValidateSessionResponse(status: Boolean) {
        this.isCheckedIn = status
    }

    private fun getInstalledApps(apiAppList: List<SelectedApps>) = lifecycleScope.launch {
        try {
            val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val apiApps = apiAppList.filter { it.forAndroid }.map { it.value }.toSet()

            val list = mutableListOf<ApplicationInfo>()
            apiApps.forEach { a ->
                allApps.forEach { s ->
                    if (a == s.packageName) {
                        list.add(s)
                    }
                }
            }

            val adapter = AppAdapter {
                if (packageManager.getLaunchIntentForPackage(it.packageName) == null) {
                    launchAppSecured(it.packageName)
                } else {
                    launchApp(it.packageName)
                }
            }
            adapter.setAppList(list)
            binding.rvApps.adapter = adapter
            appList = ArrayList(apiApps)
            updateAppList(apiApps)
        } catch (e: Exception) {
            logE("getInstalledApps Exception in AppWorldMain activity ${e.message}")
        }
    }

    private fun createRequestBody(roomNo: String, ua: String, accessType: Int): String {
        val netflixDetails = JSONObject().apply {
            put("stbRoomNo", roomNo)
            put("ua", ua)
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
                logE("Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    logD("sessionId url success")
                } else {
                    val responseBody = response.body?.string() ?: "No response body"
                    val responseCode = response.code
                    logE("sessionId Failed to call URL. Response code: $responseCode, Response body: $responseBody")
                }
            }
        })
    }

    private fun launchApp(packageName: String) {
        try {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))

            if (packageName == Constants.NETFLIX_PACKAGE_NAME) {
                val sessionId = GuestDetails.SESSION_ID
                val url =
                    "https://stb.moviebeam.com:1930/LG/rest/content/netflixAccess/enter?sessionId=$sessionId"

                val requestBody = createRequestBody(stbRoomNo, ua, 1)

                postRequest(url, requestBody)
                NETFLIX_LAUNCHED = true;
            }

        } catch (e: Exception) {
            e.printStackTrace()
            logE("launchApp Exception in AppWorldMain activity ${e.message}")
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
            logE("launchAppSecured Exception in AppWorldMain activity ${e.message}")
        }
    }

    private fun handleAccountSetupResponse(status: Resource<AccountSetupResponse>) {
        when (status) {
            is Resource.Loading -> binding.pbLoader.toVisible()
            is Resource.Success -> {
                status.data?.let { response ->
                    getInstalledApps(response.selectedAppsList)
                    binding.pbLoader.toInvisible()
                }
            }

            else -> {
                status.errorCode?.let { appWorldViewModel.showToastMessage(getString(it)) }
            }
        }
    }

    private fun filterSystemApps(apps: List<ApplicationInfo>): List<ApplicationInfo> {
        return apps.filter {
            !isSystemApp(it)
        }
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun showPopup() {
        val popupBinding = PopupLayoutBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupBinding.btnOk.requestFocus()
        popupBinding.btnOk.background = getGradientColor()
        val blurView = View(this)
        blurView.setBackgroundColor(Color.parseColor("#80000000"))
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.addContentView(blurView, params)

        popupWindow.showAtLocation(popupBinding.root, Gravity.CENTER, 0, 0)

        popupBinding.btnOk.setOnClickListener {
            popupWindow.dismiss()
            (blurView.parent as? ViewGroup)?.removeView(blurView)
        }

        popupBinding.tvPopupText.text = getString(R.string.app_world_clear_credentials_message)
    }

    private fun updateAppList(appList: Set<String>) {
        lifecycleScope.launch {
            preferenceDataStoreHelper.putPreference(
                PreferenceDataStoreConstants.APP_LIST_KEY,
                appList
            )
        }
    }

    private fun initializeDatastoreParams() {
        lifecycleScope.launch {
            stbRoomNo = getStbRoomNo()
            ua = getUa()
        }
    }

    private suspend fun getStbRoomNo(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.STB_ROOM_NO_KEY,
            ""
        )
    }

    private suspend fun getUa(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.UA,
            ""
        )
    }

    fun handleBackClick() {
        finish()
    }

    companion object {
        var NETFLIX_LAUNCHED = false
    }

}
