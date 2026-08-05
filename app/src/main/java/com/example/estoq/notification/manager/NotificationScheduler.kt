package com.example.estoq.notification.manager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.estoq.notification.worker.InsightsWorker
import com.example.estoq.notification.worker.LowStockMonitorWorker
import com.example.estoq.notification.worker.ReminderWorker
import com.example.estoq.notification.worker.SalesSummaryWorker
import com.example.estoq.notification.worker.WeeklySummaryWorker
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleAll() {
        scheduleLowStockMonitor()
        scheduleInsights()
        scheduleWeeklySummary()
        scheduleSalesSummary()
        scheduleReminder()
    }

    private fun scheduleLowStockMonitor() {
        val request = PeriodicWorkRequestBuilder<LowStockMonitorWorker>(
            6, TimeUnit.HOURS,
            2, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            LOW_STOCK_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun scheduleInsights() {
        val request = PeriodicWorkRequestBuilder<InsightsWorker>(
            7, TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            INSIGHTS_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun scheduleReminder() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun scheduleWeeklySummary() {
        val request = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(
            7, TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WEEKLY_SUMMARY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun scheduleSalesSummary() {
        val request = PeriodicWorkRequestBuilder<SalesSummaryWorker>(
            30, TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            SALES_SUMMARY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleAdhocLowStockCheck() {
        val request = OneTimeWorkRequestBuilder<LowStockMonitorWorker>()
            .build()
        workManager.enqueueUniqueWork(
            "adhoc_low_stock",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    companion object {
        const val LOW_STOCK_WORK_NAME = "low_stock_monitor"
        const val INSIGHTS_WORK_NAME = "insights_generator"
        const val REMINDER_WORK_NAME = "inventory_reminder"
        const val WEEKLY_SUMMARY_WORK_NAME = "weekly_summary"
        const val SALES_SUMMARY_WORK_NAME = "sales_monthly_summary"
    }
}
