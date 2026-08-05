package com.example.estoq.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.estoq.notification.manager.NotificationDispatcher
import com.example.estoq.notification.model.NotificationEvent
import com.example.estoq.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SalesSummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.getOrNull() ?: return Result.failure()
        val repository = koin.get<NotificationRepository>()
        val dispatcher = koin.get<NotificationDispatcher>()

        val (start, end) = previousMonthRange()
        val sales = repository.getSalesBetween(start, end).first()
        if (sales.isEmpty()) return Result.success()

        val monthLabel = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
            .format(Calendar.getInstance().apply { timeInMillis = start }.time)

        dispatcher.dispatch(
            NotificationEvent.SalesSummary(
                count = sales.size,
                totalValue = sales.sumOf { it.totalValue },
                monthLabel = monthLabel
            )
        )

        return Result.success()
    }

    private fun previousMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, -1)
        val start = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis - 1
        return start to end
    }
}
