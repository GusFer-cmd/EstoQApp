package com.example.estoq.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.model.NotificationEvent
import com.example.estoq.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext

class WeeklySummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.getOrNull() ?: return Result.failure()
        val repository = koin.get<NotificationRepository>()
        val dispatcher = koin.get<NotificationDispatcher>()

        val items = repository.getLowStockItems(5).first()
        val lowStockCount = items.count { it.stockQuantity in 1..5 }
        val zeroStockCount = items.count { it.stockQuantity == 0 }

        if (lowStockCount > 0 || zeroStockCount > 0) {
            dispatcher.dispatch(NotificationEvent.WeeklySummary(lowStockCount, zeroStockCount))
        }

        return Result.success()
    }
}
