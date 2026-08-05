package com.example.estoq.notification.model

import com.example.estoq.data.Model.Item.Item

sealed interface NotificationEvent {
    data class LowStock(val items: List<Item>) : NotificationEvent
    data class ZeroStock(val item: Item) : NotificationEvent
    data class SaleCreated(
        val saleId: Long,
        val clientName: String,
        val total: Double,
        val itemCount: Int
    ) : NotificationEvent

    data class StockRestored(val itemNames: List<String>) : NotificationEvent
    data class SaleDeleted(
        val saleId: Long,
        val clientName: String,
        val total: Double
    ) : NotificationEvent
    data class WeeklySummary(val lowStockCount: Int, val zeroStockCount: Int) : NotificationEvent
    data class RestockSuggestion(val items: List<Item>) : NotificationEvent
    data class UnsoldItems(val items: List<Item>, val days: Int) : NotificationEvent
    data class SalesSummary(
        val count: Int,
        val totalValue: Double,
        val monthLabel: String
    ) : NotificationEvent
    data object InventoryReminder : NotificationEvent
}

object NotificationIds {
    const val LOW_STOCK_SUMMARY = 100
    const val LOW_STOCK_BASE = 1000
    const val SALE_SUMMARY = 200
    const val SALE_BASE = 2000
    const val INSIGHTS_SUMMARY = 300
    const val RESTOCK_SUGGESTION = 3001
    const val UNSOLD_ITEMS = 3002
    const val SALES_MONTHLY_SUMMARY = 201
    const val INVENTORY_REMINDER = 4000
}

object NotificationGroups {
    const val LOW_STOCK = "group_low_stock"
    const val SALES = "group_sales"
    const val INSIGHTS = "group_insights"
}
