package com.example.estoq.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.model.NotificationEvent
import com.example.estoq.notification.repository.NotificationRepository
import org.koin.core.context.GlobalContext

class InsightsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.getOrNull() ?: return Result.failure()
        val repository = koin.get<NotificationRepository>()
        val dispatcher = koin.get<NotificationDispatcher>()

        val restockItems = repository.getRestockSuggestions()
        if (restockItems.isNotEmpty()) {
            dispatcher.dispatch(NotificationEvent.RestockSuggestion(restockItems))
        }

        val unsoldItems = repository.getUnsoldItems(30)
        if (unsoldItems.isNotEmpty()) {
            dispatcher.dispatch(NotificationEvent.UnsoldItems(unsoldItems, 30))
        }

        return Result.success()
    }
}
