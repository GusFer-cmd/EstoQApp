package com.example.estoq.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.model.NotificationEvent
import org.koin.core.context.GlobalContext

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.getOrNull() ?: return Result.failure()
        val dispatcher = koin.get<NotificationDispatcher>()

        dispatcher.dispatch(NotificationEvent.InventoryReminder)

        return Result.success()
    }
}
