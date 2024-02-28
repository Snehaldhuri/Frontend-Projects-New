package com.diipl.moviebeam.ui.serial_info

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivitySerialBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.kaping.RegisterSTBActivity
import com.diipl.moviebeam.ui.kappingservice.Actions
import com.diipl.moviebeam.ui.kappingservice.EndlessService
import com.diipl.moviebeam.ui.loggerService.LoggingService
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.log
import com.diipl.moviebeam.utils.observe
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

class SerialActivity : BaseActivity() {

    private lateinit var binding: ActivitySerialBinding
    private val serialViewModel: SerialViewModel by viewModels()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    private lateinit var loggingService: LoggingService

    private var isServiceBound = false


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LoggingService.LoggingServiceBinder
            loggingService = binder.getService()
            isServiceBound = true
            loggingService.startWebSocket()
        }


        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }


    override fun observeViewModel() {
        observe(serialViewModel.stbStatusLiveData, ::handleStbStatusResponse)
    }

    override fun initViewBinding() {
        binding = ActivitySerialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceDataStoreHelper = PreferenceDataStoreHelper(this)
        serialViewModel.getDataFromDataStore(preferenceDataStoreHelper)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            preferenceDataStoreHelper.getPreference(
                PreferenceDataStoreConstants.IS_SERIAL_NO_TAKEN_KEY,
                false
            ).collectIndexed { index, value ->
                if (index == 0) {
                    handleDataStoreResponse(value)
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    showSerialNumberDialog()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun handleDataStoreResponse(b: Boolean) {
        if (b) {
            serialViewModel.getStbStatusFromDataStore(preferenceDataStoreHelper)
        } else {
            showSerialNumberDialog()
        }
        actionOnService(Actions.START)
    }

    private fun handleStbStatusResponse(isStbRegistered: Boolean) {
        if (isStbRegistered) {
            startActivity(Intent(this, STBDetailsActivity::class.java))
            LoggingService.sendMessageToWebSocket("In App Loader create ","98")
        } else {
            startActivity(Intent(this, RegisterSTBActivity::class.java))
            LoggingService.sendMessageToWebSocket("Showing Landing Page","99")

        }
        finish()
    }

    private fun showSerialNumberDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Serial Number")

        // Serial No :- 29221HFGN30WLA, 26271HFGN11NHH

        val input = EditText(this)
        var serialNo: String
        input.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        input.imeOptions = EditorInfo.IME_ACTION_DONE
        input.setOnFocusChangeListener { view, isFocused ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (!isFocused) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } else {
                imm.showSoftInput(view, 0)
            }
        }
        builder.setView(input)

        // TODO Uncomment this before release
        /*if (BuildConfig.DEBUG) {
            input.setText("26271HFGN11NHH")
            input.clearFocus()
        }*/

        builder.setPositiveButton("OK") { dialog, which ->
            serialNo = input.text.toString().uppercase()
            Constants.SERIAL_NO = serialNo
            Constants.UA = "21$serialNo"
            serialViewModel.setDataInDataStore(
                preferenceDataStoreHelper,
                true,
                serialNo,
                Constants.UA
            )
            startActivity(Intent(this, RegisterSTBActivity::class.java))
            finish()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
        }

        builder.show()
    }
    private fun bindLoggingService() {
        val serviceIntent = Intent(this, LoggingService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
    override fun onStart() {
        super.onStart()
        bindLoggingService()
        // Start LoggingService if not already running
        startService(Intent(this, LoggingService::class.java))
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    private fun actionOnService(action: Actions) {
        if (!EndlessService.isServiceStarted) {
            Intent(this, EndlessService::class.java).also {
                it.action = action.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    log("Starting the service in >=26 Mode")
                    startForegroundService(it)
                    return
                } else {
                    log("Starting the service in < 26 Mode")
                    startService(it)
                }
            }
        }
    }



}
