package com.diipl.moviebeam.ui.appworld

import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.diipl.moviebeam.R
import com.diipl.moviebeam.databinding.AppCardBinding


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

        binding.cardApp.setOnFocusChangeListener { _, isFocused ->
            var anim: Animation =
                AnimationUtils.loadAnimation(parent.context, R.anim.scale_out_animation)
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

        holder.binding.cardApp.postDelayed({
            if (position == 0) {
                holder.binding.cardApp.requestFocus()
            }
        }, 1)
        holder.binding.tvAppName.text = context.packageManager.getApplicationLabel(item)
        holder.binding.ivAppIcon.setImageDrawable(context.packageManager.getApplicationBanner(item))
        holder.binding.cardApp.setOnClickListener {
            onItemClicked(item)
        }
    }

    fun setAppList(appList: List<ApplicationInfo>) {
        this.appList = appList
    }

}