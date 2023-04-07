package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0



    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val acceptIntent = Intent(applicationContext,MainActivity::class.java)
        val acceptPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            REQUEST_CODE,
            acceptIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val cloudImage = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.cloud
        )
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(cloudImage)
            .bigLargeIcon(null)

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.channel_name)
        )
            .setSmallIcon(R.drawable.cloud)
            .setContentTitle(applicationContext.resources.getString(R.string.channel_name))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setChannelId(applicationContext.getString(R.string.channel_id))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(bigPictureStyle)
            .setLargeIcon(cloudImage)
            .addAction(
                R.drawable.cloud,
                applicationContext.getString(R.string.notification_button),
                acceptPendingIntent
            )
            .setShowWhen(true)

        notify(NOTIFICATION_ID, builder.build())
    }

    fun NotificationManager.cancelNotification() {
        cancelAll()
    }
