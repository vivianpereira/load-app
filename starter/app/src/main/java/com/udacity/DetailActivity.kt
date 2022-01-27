package com.udacity

import android.app.DownloadManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.MainActivity.Companion.PARAM_FILE_NAME
import com.udacity.MainActivity.Companion.PARAM_STATUS
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val status = intent.getStringExtra(PARAM_STATUS)
        val fileName = intent.getStringExtra(PARAM_FILE_NAME)

        file_name_text.text = fileName
        status_text.text = status

        if (status == getString(R.string.notification_button_success)) {
            status_text.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        } else {
            status_text.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

        button_detail_screen.setOnClickListener {
            finish()
        }
    }
}
