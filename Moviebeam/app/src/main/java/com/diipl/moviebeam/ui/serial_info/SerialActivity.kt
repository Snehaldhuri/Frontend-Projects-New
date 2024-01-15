package com.diipl.moviebeam.ui.serial_info

import DataStoreManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.diipl.moviebeam.R
import com.diipl.moviebeam.ui.stbdetail.STBDetailsActivity


class SerialActivity : AppCompatActivity() {


    lateinit var tv_serial: TextView
    lateinit var userManager: DataStoreManager

    var serial = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serial)


        tv_serial = findViewById(R.id.tv_serial)
        userManager = DataStoreManager(this@SerialActivity)

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Title")


        val input = EditText(this)
        var m_Text: String
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)


        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            m_Text = input.text.toString()
            val bundle = Bundle()
            var i: Intent = Intent()
            //Add your data from getFactualResults method to bundle
            bundle.putString("serial", m_Text)
            //Add the bundle to the intent
            i.putExtras(bundle)
            startActivity(Intent(this@SerialActivity,STBDetailsActivity::class.java))

        })
        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }
}
