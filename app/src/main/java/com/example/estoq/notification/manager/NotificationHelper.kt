package com.example.estoq.notification.manager

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val appContext = context.applicationContext

    @SuppressLint("MissingPermission")
    fun notify(id: Int, notification: Notification) {
        if (NotificationManagerCompat.from(appContext).areNotificationsEnabled()) {
            notificationManager.notify(id, notification)
        }
    }

    @SuppressLint("MissingPermission")
    fun cancel(id: Int) {
        if (NotificationManagerCompat.from(appContext).areNotificationsEnabled()) {
            notificationManager.cancel(id)
        }
    }

    fun build(
        channelId: String,
        title: String,
        text: String,
        style: NotificationCompat.Style? = null,
        actions: List<NotificationCompat.Action> = emptyList(),
        groupKey: String? = null,
        intent: PendingIntent? = null,
        autoCancel: Boolean = true,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        smallIcon: Int = android.R.drawable.ic_dialog_info
    ): Notification {
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(autoCancel)
            .setPriority(priority)
            .setContentIntent(intent)
            .setGroup(groupKey)
            .setGroupSummary(false)
        if (style != null) builder.setStyle(style)
        for (action in actions) builder.addAction(action)
        return builder.build()
    }

    fun buildSummary(
        channelId: String,
        groupKey: String,
        title: String,
        text: String,
        intent: PendingIntent? = null
    ): Notification {
        return NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(groupKey)
            .setGroupSummary(true)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .setContentIntent(intent)
            .build()
    }
}
