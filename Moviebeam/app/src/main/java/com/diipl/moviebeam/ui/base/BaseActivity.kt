package com.diipl.moviebeam.ui.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.utils.PanelConstants
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

private const val TAG = "BaseActivity"
abstract class BaseActivity : AppCompatActivity() {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()
//        CustomThreadExecutor()
    }


    override fun onResume() {
        super.onResume()

        currentActivity = this

        activityStack.add(this::class.java.simpleName)
        if (this::class.java.simpleName == MainMenuActivity::class.java.simpleName) {
            activityStack.clear()
            activityStack.add(MainMenuActivity::class.java.simpleName)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun fragmentTransaction(
        transactionType: Int,
        fragment: Fragment,
        container: Int,
        isAddToBackStack: Boolean,
        bundle: Bundle?
    ) {
        if (bundle != null) {
            fragment.arguments = bundle
        }

        val trans = supportFragmentManager.beginTransaction()
        when (transactionType) {
            ADD_FRAGMENT -> trans.add(container, fragment, fragment.javaClass.simpleName)
            REPLACE_FRAGMENT -> {
                trans.replace(container, fragment, fragment.javaClass.simpleName)
                if (isAddToBackStack) trans.addToBackStack(null)
            }
        }
        trans.commit()
    }

    class CustomThreadExecutor {

        private lateinit var scheduledExecutorService: ScheduledExecutorService
        private lateinit var scheduledFuture: ScheduledFuture<*>
        var counter = 0

        init {
            //Start Scheduler as required
            startScheduler()
        }


        fun startScheduler() {
            scheduledExecutorService = Executors.newScheduledThreadPool(2)

            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(
                { tempImageFetch() }, 0, 60, TimeUnit.SECONDS
            )
        }

        fun shutdownScheduler() {
            //Stop before exit the app or when necessary
            scheduledExecutorService.shutdownNow()

        }

        private fun tempImageFetch() {
            //TODO call API
            counter++
            Log.d("counter", counter.toString())
        }
    }

    companion object {
        const val ADD_FRAGMENT = 0
        const val REPLACE_FRAGMENT = 1
        var currentActivity : Activity? = null
        val activityStack: MutableList<String?> = mutableListOf()
    }
}
