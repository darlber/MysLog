package com.example.myslog

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.myslog.utils.TimerService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MysLogApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            TimerService.CHANNEL_ID,
            "Workout Timer",
            NotificationManager.IMPORTANCE_DEFAULT
        ).also {
            it.setSound(null, null)
        }
        val alertChannel = NotificationChannel(
            TimerService.ALERT_CHANNEL_ID,
            "Workout Timer Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).also {
            it.enableVibration(true)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(alertChannel)
    }

}