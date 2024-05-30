package com.diipl.moviebeam.ui.hotelinfo

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.diipl.moviebeam.utils.loadImagesWithGlideExt

class ViewPagerAdapter(
    private val imageResources: List<String>?,
    private val viewPager: ViewPager
) : PagerAdapter() {

    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val slideDelay = 5 * 1000L

    override fun getCount(): Int = imageResources?.size ?: 0

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(container.context)
        imageResources?.get(position)?.let {
            imageView.loadImagesWithGlideExt(it)
        }
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        container.addView(imageView)
        return imageView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, slideDelay)

    }

    fun stopAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable)
    }

    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            if (currentPage == imageResources?.size) {
                currentPage = 0
//                stopAutoSlide()
            }
            viewPager.setCurrentItem(currentPage++, true)
            handler.postDelayed(this, slideDelay)
        }
    }

}
