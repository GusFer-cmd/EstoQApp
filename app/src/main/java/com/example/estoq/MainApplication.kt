package com.example.estoq

import android.app.Application
import com.example.estoq.koin.appModules
import com.example.estoq.notification.channel.NotificationChannelManager
import com.example.estoq.notification.manager.NotificationScheduler
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    private val channelManager: NotificationChannelManager by inject()
    private val scheduler: NotificationScheduler by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModules)
        }

        channelManager.setup()
        scheduler.scheduleAll()
    }
}
