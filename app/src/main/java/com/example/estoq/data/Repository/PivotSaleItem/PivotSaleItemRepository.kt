package com.example.estoq.data.Repository.PivotSaleItem

import com.example.estoq.data.Dao.PivotSaleItem.PivotSaleItemDao
import com.example.estoq.data.Exceptions.SaleArchive.SaleArchiveException
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.PivotSaleItem.ItemSalesSummary
import com.example.estoq.data.Model.PivotSaleItem.MonthlyProfitSummary
import com.example.estoq.data.Model.PivotSaleItem.PivotSaleItem
import com.example.estoq.data.Repository.Item.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PivotSaleItemRepository(
    private val pivotSaleItemDao: PivotSaleItemDao,
    private val itemRepository: ItemRepository
) {

    fun getBestSellingItems(): Flow<List<ItemSalesSummary>> =
        pivotSaleItemDao.getBestSellingItems()

    fun getLeastSellingItems(): Flow<List<ItemSalesSummary>> =
        pivotSaleItemDao.getLeastSellingItems()

    fun getProfitByMonth(): Flow<List<MonthlyProfitSummary>> =
        pivotSaleItemDao.getProfitByMonth()

    suspend fun getLowStockItems(threshold: Int = 5): List<Item> =
        itemRepository.getLowStockItems(threshold).first()

    suspend fun getItemsBySaleArchiveId(saleArchiveId: Long): List<PivotSaleItem> {
        return try {
            pivotSaleItemDao.getItemsBySaleArchiveId(saleArchiveId)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }

    suspend fun insertAll(items: List<PivotSaleItem>) {
        try {
            pivotSaleItemDao.insertAll(items)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }

    suspend fun deleteBySaleArchiveId(saleArchiveId: Long) {
        try {
            pivotSaleItemDao.deleteBySaleArchiveId(saleArchiveId)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }
}
