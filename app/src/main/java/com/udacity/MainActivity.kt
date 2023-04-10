package com.udacity

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
            download(it)
        }
        createChannel(
            getString(R.string.channel_id),
            getString(R.string.channel_name)
        )
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                settingsAlert()
            }
        }
        return true
    }

    private fun settingsAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(getString(R.string.settings_description))
        builder.setPositiveButton(getString(R.string.ok), null)
        builder.show()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            (custom_button as LoadingButton).buttonState = ButtonState.Completed

            val status = when (intent?.getIntExtra(DownloadManager.COLUMN_STATUS, -1)) {
                DownloadManager.STATUS_SUCCESSFUL -> "Successful"
                else -> "Failure"
            }

            notificationManager.sendNotification(
                repositoryUrl = downloadUrl,
                downloadStatus = status,
                messageBody = applicationContext.getText(R.string.notification_description).toString(),
                applicationContext = applicationContext
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

    private fun download(view: View) {
        downloadUrl?.let { url ->
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
            (view as LoadingButton).buttonState = ButtonState.Loading
        } ?: run {
            Toast.makeText(applicationContext, "Please select an option", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        private const val STARTER_PROJECT_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
    }

}
