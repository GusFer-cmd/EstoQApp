package com.example.estoq.notification.manager

import android.content.Context
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_INSIGHTS
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_LOW_STOCK
import com.example.estoq.notification.channel.NotificationChannelManager.Companion.CHANNEL_SALES_OPERATIONS
import com.example.estoq.notification.model.NotificationEvent
import com.example.estoq.notification.model.NotificationGroups
import com.example.estoq.notification.model.NotificationIds
import com.example.estoq.notification.repository.NotificationRepository

class NotificationDispatcher(
    private val context: Context,
    private val factory: NotificationFactory,
    private val helper: NotificationHelper,
    private val repository: NotificationRepository
) {
    fun dispatch(event: NotificationEvent) {
        if (!shouldNotify(event)) return

        val (id, notification) = factory.create(event, context)
        helper.notify(id, notification)
        postSummaryIfNeeded(event)
        markNotified(event)
    }

    private fun shouldNotify(event: NotificationEvent): Boolean {
        return when (event) {
            is NotificationEvent.LowStock -> {
                event.items.any { !repository.wasNotified("low_stock_${it.id}") }
            }
            is NotificationEvent.ZeroStock -> {
                !repository.wasNotified("zero_stock_${event.item.id}")
            }
            else -> true
        }
    }

    private fun markNotified(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.LowStock -> {
                event.items.forEach { repository.markNotified("low_stock_${it.id}") }
            }
            is NotificationEvent.ZeroStock -> {
                repository.markNotified("zero_stock_${event.item.id}")
            }
            else -> {}
        }
    }

    private fun postSummaryIfNeeded(event: NotificationEvent) {
        val summaryInfo = summaryInfoFor(event) ?: return
        val (groupId, channelId, title, text) = summaryInfo
        val (summaryId, summary) = factory.createSummary(groupId, channelId, title, text, context)
        helper.notify(summaryId, summary)
    }

    private data class SummaryInfo(
        val groupKey: String,
        val channelId: String,
        val title: String,
        val text: String
    )

    private fun summaryInfoFor(event: NotificationEvent): SummaryInfo? {
        return when (event) {
            is NotificationEvent.LowStock -> SummaryInfo(
                NotificationGroups.LOW_STOCK, CHANNEL_LOW_STOCK,
                "${event.items.size} itens com estoque baixo",
                "${zeroCount(event.items)} zerados, ${lowCount(event.items)} abaixo do mínimo"
            )
            is NotificationEvent.ZeroStock -> SummaryInfo(
                NotificationGroups.LOW_STOCK, CHANNEL_LOW_STOCK,
                "Estoque crítio", "1 item zerado"
            )
            is NotificationEvent.SaleCreated -> SummaryInfo(
                NotificationGroups.SALES, CHANNEL_SALES_OPERATIONS,
                "Nova venda", "Clique para ver detalhes"
            )
            is NotificationEvent.StockRestored -> SummaryInfo(
                NotificationGroups.SALES, CHANNEL_SALES_OPERATIONS,
                "Estoque restaurado", "${event.itemNames.size} Venda cancelada, estoque restaurado"
            )
            is NotificationEvent.RestockSuggestion -> SummaryInfo(
                NotificationGroups.INSIGHTS, CHANNEL_INSIGHTS,
                "Sugestões de reposição", "${event.items.size} itens precisam de atenção"
            )
            is NotificationEvent.UnsoldItems -> SummaryInfo(
                NotificationGroups.INSIGHTS, CHANNEL_INSIGHTS,
                "Itens sem saída", "${event.items.size} itens sem vendas há ${event.days} dias"
            )
            is NotificationEvent.WeeklySummary -> null
            is NotificationEvent.InventoryReminder -> null
            is NotificationEvent.SaleDeleted -> null
            is NotificationEvent.SalesSummary -> null
        }
    }

    private fun zeroCount(items: List<com.example.estoq.data.Model.Item.Item>) =
        items.count { it.stockQuantity == 0 }

    private fun lowCount(items: List<com.example.estoq.data.Model.Item.Item>) =
        items.count { it.stockQuantity in 1..5 }

    fun cancelGroup(groupKey: String) {
        when (groupKey) {
            NotificationGroups.LOW_STOCK -> helper.cancel(NotificationIds.LOW_STOCK_SUMMARY)
            NotificationGroups.SALES -> helper.cancel(NotificationIds.SALE_SUMMARY)
            NotificationGroups.INSIGHTS -> helper.cancel(NotificationIds.INSIGHTS_SUMMARY)
        }
    }
}
