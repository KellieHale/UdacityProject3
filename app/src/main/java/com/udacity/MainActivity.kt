package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    private var downloadUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        button_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.glide -> {
                    downloadUrl = GLIDE_URL
                }
                R.id.loadapp -> {
                    downloadUrl = STARTER_PROJECT_URL
                }
                R.id.retrofit -> {
                    downloadUrl = RETROFIT_URL
                }
            }
        }
        custom_button.setOnClickListener {
            download()
        }
        createChannel(
            getString(R.string.channel_id),
            getString(R.string.channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            notificationManager.sendNotification(
                applicationContext.getText(R.string.notification_description).toString(),
                applicationContext
            )
        }
    }
    private fun createChannel(channelId: String, channelName: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.enableVibration(true)
                notificationChannel.description = R.string.notification_description.toString()
                notificationManager.createNotificationChannel(notificationChannel)
            }
    }

    private fun download() {
        downloadUrl.let { url ->
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
        if (downloadUrl != "") {
            Toast.makeText(applicationContext, "Please select an option", Toast.LENGTH_LONG).show()
            cancelDownload()
        }
    }

    private fun cancelDownload() {
        TODO("Not yet implemented")
    }

    companion object {

        private const val STARTER_PROJECT_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

}
