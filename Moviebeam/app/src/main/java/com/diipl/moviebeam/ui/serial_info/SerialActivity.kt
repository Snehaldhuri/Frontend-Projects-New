package com.diipl.moviebeam.ui.serial_info

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.viewModels
import com.diipl.moviebeam.Constants
import com.diipl.moviebeam.data.local.PreferenceDataStoreHelper
import com.diipl.moviebeam.databinding.ActivitySerialBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity
import com.diipl.moviebeam.utils.observe


class SerialActivity : BaseActivity() {

    private lateinit var binding: ActivitySerialBinding
    private val serialViewModel: SerialViewModel by viewModels()
    private lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper
    override fun observeViewModel() {
        observe(serialViewModel.serialNoTakenLiveData, ::handleDataStoreResponse)
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

    private fun handleDataStoreResponse(b: Boolean) {
        if (b) {
            startActivity(Intent(this, STBDetailsActivity::class.java))
            finish()
        } else {
            showSerialNumberDialog()
        }
    }

    private fun showSerialNumberDialog() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter Serial Number")

        // Serial No :- 29221HFGN30WLA

        val input = EditText(this)
        var m_Text: String
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)


        builder.setPositiveButton("OK") { dialog, which ->
            m_Text = input.text.toString()
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
