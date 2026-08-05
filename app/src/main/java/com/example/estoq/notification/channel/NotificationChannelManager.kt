package com.example.estoq.notification.channel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

class NotificationChannelManager(private val context: Context) {

    fun setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannels(listOf(lowStock(), salesOperations(), reminders(), insights(), system()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun lowStock() = NotificationChannel(
        CHANNEL_LOW_STOCK, "Estoque Baixo", NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Alertas de itens com estoque baixo ou zerado"
        enableVibration(true)
        enableLights(true)
        setShowBadge(true)
        setSound(
            Settings.System.DEFAULT_NOTIFICATION_URI,
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun salesOperations() = NotificationChannel(
        CHANNEL_SALES_OPERATIONS, "Vendas e Operações", NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Confirmações de vendas e movimentações de estoque"
        enableVibration(true)
        enableLights(true)
        setSound(
            Settings.System.DEFAULT_NOTIFICATION_URI,
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reminders() = NotificationChannel(
        CHANNEL_REMINDERS, "Lembretes", NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Lembretes para revisão de estoque e inventário"
        enableVibration(true)
        enableLights(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insights() = NotificationChannel(
        CHANNEL_INSIGHTS, "Insights Inteligentes", NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = "Recomendações e análises inteligentes sobre o estoque"
        enableVibration(false)
        setSound(null, null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun system() = NotificationChannel(
        CHANNEL_SYSTEM, "Sistema", NotificationManager.IMPORTANCE_MIN
    ).apply {
        description = "Notificações do sistema sobre atualizações e manutenção"
        enableVibration(false)
        setSound(null, null)
    }

    companion object {
        const val CHANNEL_LOW_STOCK = "low_stock"
        const val CHANNEL_SALES_OPERATIONS = "sales_operations"
        const val CHANNEL_REMINDERS = "reminders"
        const val CHANNEL_INSIGHTS = "insights"
        const val CHANNEL_SYSTEM = "system"
    }
}
