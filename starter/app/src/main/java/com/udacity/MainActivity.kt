package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedURL: String = ""
    private var fileName = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
//    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        radio_group_buttons.setOnCheckedChangeListener { _, index ->
            when (index) {
                1 -> {
                    selectedURL = URL_GLIDE
                    fileName = getString(R.string.glide_download)
                }
                2 -> {
                    selectedURL = URL_UDACITY
                    fileName = getString(R.string.loadApp_download)
                }
                else -> {
                    selectedURL = URL_RETROFIT
                    fileName = getString(R.string.retrofit_download)
                }
            }
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            when {
                selectedURL.isEmpty() -> {
                    Toast.makeText(
                        applicationContext, getString(R.string.toast_message_selected_file),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> download()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            notificationManager = ContextCompat.getSystemService(
                context!!,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.createChannel()

            if (downloadID == id) {
                custom_button.buttonState = ButtonState.Completed

                val query = DownloadManager.Query().setFilterById(id)
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                        notificationManager.sendNotification(
                            getString(R.string.notification_description_success),
                            getString(R.string.notification_button_success)
                        )
                    } else {
                        notificationManager.sendNotification(
                            getString(R.string.notification_description_error),
                            getString(R.string.notification_button_fail)
                        )
                    }
                }
            }
        }
    }

    fun NotificationManager.sendNotification(messageBody: String, status: String) {

        val contentIntent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(PARAM_STATUS, status)
            putExtra(PARAM_FILE_NAME, fileName)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)

            .setSmallIcon(R.drawable.ic_cloud_download_24)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .addAction(
                NotificationCompat.Action.Builder(
                    android.R.drawable.ic_input_get,
                    getString(R.string.notification_check_the_status),
                    contentPendingIntent
                ).build()
            )
            .setAutoCancel(true)

        notify(NOTIFICATION_ID, builder.build())
    }

    private fun NotificationManager.createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)
            createNotificationChannel(notificationChannel)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "load_app_download"
        private const val NOTIFICATION_ID = 123
        const val PARAM_STATUS = "PARAM_STATUS"
        const val PARAM_FILE_NAME = "PARAM_FILE_NAME"
    }

}
