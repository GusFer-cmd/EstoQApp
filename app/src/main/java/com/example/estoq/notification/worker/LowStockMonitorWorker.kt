package com.example.estoq.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.model.NotificationEvent
import com.example.estoq.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext

class LowStockMonitorWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.getOrNull() ?: return Result.failure()
        val repository = koin.get<NotificationRepository>()
        val dispatcher = koin.get<NotificationDispatcher>()

        val allLowStock: List<Item> = repository.getLowStockItems(5).first()
        val zeroStockItems = allLowStock.filter { item -> item.stockQuantity == 0 }
        val lowItems = allLowStock.filter { item -> item.stockQuantity in 1..5 }

        if (lowItems.isNotEmpty()) {
            dispatcher.dispatch(NotificationEvent.LowStock(lowItems))
        }

        for (item in zeroStockItems) {
            dispatcher.dispatch(NotificationEvent.ZeroStock(item))
        }

        return Result.success()
    }
}
