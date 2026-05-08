package com.iberdrola.practicas2026.MarPG.ui.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeResource
import androidx.core.app.NotificationCompat
import com.iberdrola.practicas2026.MarPG.R
import kotlin.random.Random

class NotificationHandler(private val context: Context) {

    private val notificationManager =
        context.getSystemService(NotificationManager::class.java)

    private val notificationChannelID = "app_notification_channel_id"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            notificationChannelID,
            "Notificaciones",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Canal de notificaciones de la aplicación"
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun showSimpleNotification(contentTitle: String, contentText: String) {
        val bitmap = decodeResource(context.resources, R.drawable.iberdrola)
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}
