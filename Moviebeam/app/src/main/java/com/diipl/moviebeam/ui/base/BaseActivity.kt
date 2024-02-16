package com.diipl.moviebeam.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import com.diipl.moviebeam.ui.mainmenu.MainMenuActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity() {

    private val TAG = "BaseActivity"
    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    private val homePressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            intent.let {
                if (it.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                    val reason = it.getStringExtra("reason")
                    if (reason == "homekey") {
                        if (currentActivity?.javaClass?.simpleName != MainMenuActivity::class.java.simpleName) {
                            startActivity(Intent(context, MainMenuActivity::class.java).also { i->
                                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            Log.e(TAG, "onReceive: 0")
                            return
                        } else {
                            Log.e(TAG, "onReceive: 1")
                            return
                        }
                    } else {
                        Log.e(TAG, "onReceive: 2")
                        return
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()
//        CustomThreadExecutor()

        val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(homePressReceiver, filter)

    }


    override fun onResume() {
        super.onResume()
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
        val activityStack: MutableList<String?> = mutableListOf()
    }
}
