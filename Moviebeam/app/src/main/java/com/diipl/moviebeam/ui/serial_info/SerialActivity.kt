package com.diipl.moviebeam.ui.serial_info

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import com.diipl.moviebeam.databinding.ActivitySerialBinding
import com.diipl.moviebeam.ui.base.BaseActivity
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity


class SerialActivity : BaseActivity() {

    private lateinit var binding: ActivitySerialBinding
    override fun observeViewModel() {

    }

    override fun initViewBinding() {
        binding = ActivitySerialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter Serial Number")

        // Serial No :- 29221HFGN30WLA

        val input = EditText(this)
        var m_Text: String
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)


        builder.setPositiveButton("OK") { dialog, which ->
            m_Text = input.text.toString()
            val bundle = Bundle()
            val i: Intent = Intent()
            //Add your data from getFactualResults method to bundle
            bundle.putString("serial", m_Text)
            //Add the bundle to the intent
            i.putExtras(bundle)
            startActivity(Intent(this@SerialActivity, STBDetailsActivity::class.java))

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
        }

        builder.show()
    }
}
