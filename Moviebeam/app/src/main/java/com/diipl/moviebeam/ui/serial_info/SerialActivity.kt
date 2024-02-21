package com.diipl.moviebeam.ui.serial_info

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.diipl.moviebeam.BuildConfig
import com.diipl.moviebeam.data.local.PreferenceDataStoreConstants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivitySerialBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.Constants
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch


class SerialActivity : BaseActivity() {

    private lateinit var binding: ActivitySerialBinding
    private val serialViewModel: SerialViewModel by viewModels()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    override fun observeViewModel() {
//        observe(serialViewModel.serialNoTakenLiveData, ::handleDataStoreResponse)
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
            startActivity(Intent(this, STBDetailsActivity::class.java))
            finish()
        } else {
            showSerialNumberDialog()
        }
    }

    private fun showSerialNumberDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Serial Number")


        // Serial No :- 29221HFGN30WLA, P-> 26271HFGN11NHH, C-> 14507KKWK1C017/ 507KKWK1C017

        val input = EditText(this)
        var m_Text: String
        input.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.imeOptions = EditorInfo.IME_ACTION_DONE
        builder.setView(input)

/* TODO Uncomment this before release */
        if (BuildConfig.DEBUG){
            input.setText("26271HFGN11NHH")
            input.clearFocus()
        }

        builder.setPositiveButton("OK") { dialog, which ->
            m_Text = input.text.toString().uppercase()
            Constants.SERIAL_NO = m_Text
            serialViewModel.setDataInDataStore(preferenceDataStoreHelper, true, m_Text)
            startActivity(Intent(this, STBDetailsActivity::class.java))
            finish()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
        }

        builder.show()
    }
}
