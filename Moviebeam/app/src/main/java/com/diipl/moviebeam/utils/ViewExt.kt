package com.diipl.moviebeam.utils

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.diipl.moviebeam.R
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

fun String.isServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (this.equals(service.service.className))
            return true
    }
    return false
}


fun View.showKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.toVisible() {
    if (this.visibility != View.VISIBLE)
        this.visibility = View.VISIBLE
}

fun View.isVisible() : Boolean {
    return this.visibility == View.VISIBLE
}

fun View.isInvisible() : Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.isGone() : Boolean {
    return this.visibility == View.GONE
}

fun View.toDelayVisible() {
    if (this.visibility != View.VISIBLE) {
        this.postDelayed({
            this.visibility = View.VISIBLE
        }, 200)
    }
}

fun View.toAnimVisible() {
    if (this.visibility != View.VISIBLE) {
        this.post {
            this.animation =
                AnimationUtils.loadAnimation(this.context, android.R.anim.slide_in_left)
        }
    }
}


fun View.toGone() {
    if (this.visibility != View.GONE)
        this.visibility = View.GONE
}

fun View.toInvisible() {
    if (this.visibility != View.INVISIBLE)
        this.visibility = View.INVISIBLE
}


/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).run {
        show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<SingleEvent<Any>>,
    timeLength: Int
) {
    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            when (it) {
                is String -> {
                    hideKeyboard()
                    showSnackbar(it, timeLength)
                }

                is Int -> {
                    hideKeyboard()
                    showSnackbar(this.context.getString(it), timeLength)
                }

                else -> {
                }
            }

        }
    })
}

fun View.showToast(
    lifecycleOwner: LifecycleOwner,
    ToastEvent: LiveData<SingleEvent<Any>>,
    timeLength: Int
) {

    ToastEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            when (it) {
                is String -> Toast.makeText(this.context, it, timeLength).show()
                is Int -> Toast.makeText(this.context, this.context.getString(it), timeLength)
                    .show()

                else -> {
                }
            }
        }
    })
}

fun ImageView.loadImagesWithGlideExtFomAssets(path: String) {
    try {
        val inputStream = context.assets.open(path)
        val drawable = Drawable.createFromStream(inputStream, null)
        Glide.with(this)
            .load(drawable)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
        inputStream.close()
    } catch (e: IOException) {
        Log.e("Glide", "Failed to load image from assets: $path", e)
    }
}

fun ImageView.loadImagesWithGlideExt(url: String) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }
}

fun ImageView.loadImagesWithGlideExtLogo(url: String?) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.logo_default)
            .into(this)
    }
}

fun ImageView.loadImagesWithGlideExtPoster(url: String) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.default_poster)
            .into(this)
    }
}

fun ImageView.loadImagesWithGlideExtSushi(url: String) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.default_sushi)
            .into(this)
    }
}

fun ImageView.loadImagesWithGlideExtHsCard(url: String) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.hs_card_default)
            .error(R.drawable.hs_card_default)
            .into(this)
    }
}

fun ImageView.loadImagesWithGlideExtHS(url: String) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.hs_default)
            .into(this)
    }
}

fun ImageView.loadImagesWithGlideExtLA(url: String) {
    if (url != null) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.la_default)
            .into(this)
    }
}

fun log(msg: String) {
    Log.d("ENDLESS-SERVICE", msg)
}





