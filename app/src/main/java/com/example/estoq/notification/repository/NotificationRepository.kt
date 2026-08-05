package com.example.estoq.notification.repository

import android.content.Context
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.SaleArchive.SaleArchive
import com.example.estoq.data.Repository.Item.ItemRepository
import com.example.estoq.data.Repository.PivotSaleItem.PivotSaleItemRepository
import com.example.estoq.data.Repository.SaleArchive.SaleArchiveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NotificationRepository(
    private val context: Context,
    private val itemRepository: ItemRepository,
    private val saleArchiveRepository: SaleArchiveRepository,
    private val pivotSaleItemRepository: PivotSaleItemRepository
) {

    private val prefs by lazy {
        context.getSharedPreferences("notification_log", Context.MODE_PRIVATE)
    }

    fun getLowStockItems(threshold: Int = 5): Flow<List<Item>> = itemRepository.getLowStockItems(threshold)

    fun getZeroStockItems(): Flow<List<Item>> = itemRepository.getLowStockItems(0)

    fun getSalesBetween(startMillis: Long, endMillis: Long): Flow<List<SaleArchive>> =
        saleArchiveRepository.getSalesBetween(startMillis, endMillis)

    suspend fun getUnsoldItems(days: Int): List<Item> {
        val cutoff = System.currentTimeMillis() - days * 24L * 60 * 60 * 1000
        val allItems = firstOrEmpty(itemRepository.getAll())
        if (allItems.isEmpty()) return emptyList()

        val soldItemIds = mutableSetOf<Long>()
        val allSales = firstOrEmpty(saleArchiveRepository.getAll())
        val recentSales = allSales.filter { it.createdAt >= cutoff }
        for (sale in recentSales) {
            val items = pivotSaleItemRepository.getItemsBySaleArchiveId(sale.id)
            soldItemIds.addAll(items.map { it.itemId })
        }
        return allItems.filter { it.id !in soldItemIds }
    }

    suspend fun getRestockSuggestions(): List<Item> {
        val lowStock = firstOrEmpty(itemRepository.getLowStockItems(5))
        return lowStock.filter { it.stockQuantity > 0 }
    }

    fun wasNotified(key: String): Boolean = prefs.getBoolean(key, false)

    fun markNotified(key: String) {
        prefs.edit().putBoolean(key, true).apply()
    }

    fun clearNotification(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private suspend fun <T> firstOrEmpty(flow: Flow<List<T>>): List<T> {
        return try {
            flow.first()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
