package com.diipl.moviebeam.ui.appworld

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.AppCardBinding
import com.diipl.moviebeam.utils.Constants
import com.diipl.moviebeam.utils.GuestDetails
import com.diipl.moviebeam.utils.getHeightInPercent
import com.diipl.moviebeam.utils.getWidthInPercent
import com.diipl.moviebeam.utils.showToast

class AppAdapter(
    private val onItemClicked: (ApplicationInfo) -> Unit
) :
    RecyclerView.Adapter<AppAdapter.MyViewHolder>() {

    private var appList: List<ApplicationInfo> = emptyList()

    inner class MyViewHolder(val binding: AppCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AppCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.isFocusable = true
        binding.root.isFocusableInTouchMode = true
        binding.root.setBackgroundResource(R.color.transparent)

        val params = binding.root.layoutParams
        params.width = getWidthInPercent(parent.context, 19)
        params.height = getHeightInPercent(parent.context, 24)

        binding.cardApp.setOnFocusChangeListener { _, isFocused ->
            var anim: Animation = AnimationUtils.loadAnimation(parent.context, R.anim.scale_out_animation)
            if (isFocused) {
                anim = AnimationUtils.loadAnimation(parent.context, R.anim.scale_in_animation)
            }
            binding.root.startAnimation(anim)
            anim.fillAfter = true
        }
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = appList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = appList[position]
        val context = holder.binding.root.context

        holder.binding.cardApp.post{
            if (holder.adapterPosition == 0) {
                holder.binding.cardApp.requestFocus()
            }
        }
        holder.binding.tvAppName.text = context.packageManager.getApplicationLabel(item)

        //var banner: Drawable = app.activityInfo.loadBanner(mPackageManager)
        //if (banner == null) {
        //    banner = app.activityInfo.applicationInfo.loadBanner(mPackageManager)
        //}

        val appsIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val apps: List<ResolveInfo> = context.packageManager.queryIntentActivities(appsIntent, 0)


        getBanner(context,item.packageName)
        if(context.packageManager.getApplicationBanner(item)==null){
            val icon=getBanner(context,item.packageName)
            if(icon!=null) {
                holder.binding.ivAppIcon.setImageDrawable(icon)
            }else{
                holder.binding.ivAppIcon.setImageDrawable(
                    context.packageManager.getApplicationIcon(
                        item
                    )
                )
            }

        }else {
            holder.binding.ivAppIcon.setImageDrawable(
                context.packageManager.getApplicationBanner(
                    item
                )
            )
        }
        holder.binding.cardApp.setOnClickListener {
            if((item.packageName != Constants.NETFLIX_PACKAGE_NAME) || (item.packageName == Constants.NETFLIX_PACKAGE_NAME && GuestDetails.IS_GUEST_CHECKED_IN))
                onItemClicked(item)
            else
                context.showToast(context.getString(R.string.please_contact_the_front_desk_for_assistance))
        }
    }

    private fun getBanner(context: Context, packageName: String): Drawable? {
        var banner: Drawable?=null
        val appsIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val apps: List<ResolveInfo> = context.packageManager.queryIntentActivities(appsIntent, 0)

        for (app in apps) {
            if(app.activityInfo.packageName==packageName) {

                banner = app.activityInfo.loadBanner(context.packageManager)
                if (banner == null) {
                    banner = app.activityInfo.applicationInfo.loadBanner(context.packageManager)
                }
            }
        }
        return banner
    }

    fun setAppList(appList: List<ApplicationInfo>) {
        this.appList = appList
    }

}