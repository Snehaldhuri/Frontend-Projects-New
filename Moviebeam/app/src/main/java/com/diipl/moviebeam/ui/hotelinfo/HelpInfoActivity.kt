package com.diipl.moviebeam.ui.hotelinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.diipl.moviebeam.R

class HelpInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_info)
        findViewById<Button>(R.id.btn_back).setOnClickListener{
            finish()
        }
    }
}