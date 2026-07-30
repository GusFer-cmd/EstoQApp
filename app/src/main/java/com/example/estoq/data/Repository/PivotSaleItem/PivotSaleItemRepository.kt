package com.example.estoq.data.Repository.PivotSaleItem

import com.example.estoq.data.Dao.PivotSaleItem.PivotSaleItemDao
import com.example.estoq.data.Exceptions.SaleArchive.SaleArchiveException
import com.example.estoq.data.Model.PivotSaleItem.PivotSaleItem

class PivotSaleItemRepository(private val pivotSaleItemDao: PivotSaleItemDao) {

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
