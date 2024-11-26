package com.diipl.moviebeam.ui.mainmenu

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.data.dto.btn.BtnModel
import com.diipl.moviebeam.databinding.ItemButtonBinding
import com.diipl.moviebeam.service.handler.PreferenceHandler
import com.diipl.moviebeam.ui.base.BaseActivity.Companion.currentActivity
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getInstalledAppInfo
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.openSettingsPattern
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

private const val TAG = "MainMenuBtnAdapter"

class MainMenuBtnAdapter(
    private var onMenuItemClicked: (BtnModel) -> Unit
) :
    RecyclerView.Adapter<MainMenuBtnAdapter.MyViewHolder>() {
    var itemList: List<BtnModel> = emptyList()
    lateinit var context: Context

    //Variables from datastore
    private val preferenceHandler : PreferenceHandler by lazy { PreferenceHandler(
        currentActivity!!) }
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
            holder.binding.root.setPadding(3)
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

        holder.itemView.openSettingsPattern()

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
            preferenceHandler.loadAllData()
            delay(100)
            gradientStartColor = preferenceHandler.gradientStartColor
            gradientEndColor = preferenceHandler.gradientEndColor
        }
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


