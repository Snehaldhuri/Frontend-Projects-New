package com.diipl.moviebeam.ui.mainmenu

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ItemButtonBinding
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.currentActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getInstalledAppInfo
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.handleFocusChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections

private const val TAG = "MainMenuBtnAdapter"

class MainMenuBtnAdapter(
    private var onMenuItemClicked: (BtnModel) -> Unit
) :
    RecyclerView.Adapter<MainMenuBtnAdapter.MyViewHolder>() {
    var itemList: List<BtnModel> = emptyList()
    lateinit var context: Context
    var count = 0

    //Variables from datastore
    private val preferenceDataStoreHelper = PreferenceDataStoreHelper(currentActivity!!)
    private var gradientStartColor = Constants.DEFAULTGRADIENTSTARTCOLOR
    private var gradientEndColor = Constants.DEFAULTGRADIENTENDCOLOR

    inner class MyViewHolder(val binding: ItemButtonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        initializeDatastoreParams()
        val params = binding.root.layoutParams
        params.width = getWidthInPercent(parent.context, 21)
        params.height = getHeightInPercent(parent.context, 16)

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = itemList.size


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        val app = context.getInstalledAppInfo(item.appPackageId)

        if (item.isApp && app != null) {
            holder.binding.root.setPadding(5)
            val drawable = context.packageManager.getApplicationBanner(item.appPackageId)
            holder.binding.ivAppIcon.setImageDrawable(drawable)
            holder.binding.root.setOnClickListener { onAppClicked(item.appPackageId) }

        } else {
            holder.binding.ivMenuIcon.setImageResource(item.imageResId)
            holder.binding.tvMenuTitle.text = item.title
            holder.binding.root.setOnClickListener { onMenuItemClicked(item) }
        }


        holder.binding.root.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                view.background = getGradientColor()
            } else {
                view.setBackgroundResource(R.drawable.btn_bg_gradient_default)
            }
        }

        holder.itemView.setOnKeyListener { view, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_TV_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_TV_INPUT")
            if (i == KeyEvent.KEYCODE_NAVIGATE_IN) Log.e(
                TAG,
                "onBindViewHolder: KEYCODE_NAVIGATE_IN"
            )
            if (i == KeyEvent.KEYCODE_AVR_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_AVR_INPUT")
            if (i == KeyEvent.KEYCODE_STB_INPUT) Log.e(TAG, "onBindViewHolder: KEYCODE_STB_INPUT")
            if (holder.absoluteAdapterPosition == 0) {
                if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                    count++
                    Log.e(TAG, "KEYCODE_DPAD_LEFT: $count")
                    if (count == 20) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.action = Settings.ACTION_SETTINGS
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        view.context.startActivity(intent)
                        count = 0
                    }
                }
            }
            if (i == KeyEvent.KEYCODE_DPAD_RIGHT || i == KeyEvent.KEYCODE_DPAD_DOWN || i == KeyEvent.KEYCODE_DPAD_UP) {
                count = 0
            }
            false
        }

    }

    private fun getGradientColor(): GradientDrawable {
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TR_BL,
            intArrayOf(Color.parseColor(gradientStartColor), Color.parseColor(gradientEndColor))
        )
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT

        gradientDrawable.setGradientCenter(0.0468f, 0.6542f)
        return gradientDrawable
    }

    private fun initializeDatastoreParams() {
        CoroutineScope(Dispatchers.Default).launch {
            gradientStartColor = getGradientStartColor()
            gradientEndColor = getGradientEndColor()
        }
    }

    private suspend fun getGradientStartColor(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_START_KEY,
            Constants.DEFAULTGRADIENTSTARTCOLOR
        )
    }

    private suspend fun getGradientEndColor(): String {
        return preferenceDataStoreHelper.getFirstPreference(
            PreferenceDataStoreConstants.GRADIENT_COLOR_END_KEY,
            Constants.DEFAULTGRADIENTENDCOLOR
        )
    }

    private fun onAppClicked(packageName: String) {
        if (context.packageManager.getLaunchIntentForPackage(packageName) == null) {
            launchAppSecured(packageName)
        } else {
            launchApp(packageName)
        }
    }

    private fun launchApp(packageName: String) {
        context.startActivity(
            context.packageManager.getLaunchIntentForPackage(packageName)?.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
        )
    }

    private fun launchAppSecured(packageName: String?) {
        val intent = Intent()
        intent.setPackage(packageName)
        val pm = context.packageManager
        val resolveInfoList = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
        Collections.sort(resolveInfoList, ResolveInfo.DisplayNameComparator(pm))
        if (resolveInfoList.isNotEmpty()) {
            val launchAble = resolveInfoList[0]
            val activity = launchAble.activityInfo
            val name = ComponentName(
                activity.applicationInfo.packageName,
                activity.name
            )
            val i = Intent(Intent.ACTION_VIEW)
            if (packageName != null) {
                if (!packageName.contains(".settings")) {
                    i.component = name
                } else {
                    i.action = Settings.ACTION_SETTINGS
                }
            }
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            context.startActivity(i)
        }
    }
}


