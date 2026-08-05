package com.example.estoq.koin

import com.example.estoq.notification.channel.NotificationChannelManager
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.manager.NotificationFactory
import com.example.estoq.notification.manager.NotificationHelper
import com.example.estoq.notification.manager.NotificationPermissionManager
import com.example.estoq.notification.manager.NotificationScheduler
import com.example.estoq.notification.repository.NotificationRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {

    single {
        NotificationChannelManager(androidContext())
    }

    single {
        NotificationHelper(androidContext())
    }

    single {
        NotificationFactory(get())
    }

    single {
        NotificationRepository(
            context = androidContext(),
            itemRepository = get(),
            saleArchiveRepository = get(),
            pivotSaleItemRepository = get()
        )
    }

    single {
        NotificationDispatcher(
            context = androidContext(),
            factory = get(),
            helper = get(),
            repository = get()
        )
    }

    single {
        NotificationScheduler(androidContext())
    }

    single {
        NotificationPermissionManager(androidContext())
    }
}
