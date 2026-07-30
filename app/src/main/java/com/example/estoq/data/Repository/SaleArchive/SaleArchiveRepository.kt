package com.example.estoq.data.Repository.SaleArchive

import com.example.estoq.data.Dao.SaleArchive.SaleArchiveDao
import com.example.estoq.data.Dao.SaleArchive.SalesByPeriod
import com.example.estoq.data.Exceptions.SaleArchive.SaleArchiveException
import com.example.estoq.data.Model.SaleArchive.SaleArchive
import kotlinx.coroutines.flow.Flow

class SaleArchiveRepository(private val saleArchiveDao: SaleArchiveDao) {

    fun getAll(): Flow<List<SaleArchive>> = saleArchiveDao.getAll()

    suspend fun getById(id: Long): SaleArchive? {
        return try {
            saleArchiveDao.getById(id)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }

    fun getByClientId(clientId: Long): Flow<List<SaleArchive>> = saleArchiveDao.getAllByClientId(clientId)

    suspend fun insert(saleArchive: SaleArchive): Long {
        return try {
            saleArchiveDao.insert(saleArchive)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }

    suspend fun update(saleArchive: SaleArchive) {
        try {
            saleArchiveDao.update(saleArchive)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }

    suspend fun delete(id: Long) {
        try {
            saleArchiveDao.delete(id)
        } catch (e: Exception) {
            throw SaleArchiveException.SaleArchiveUnknownException()
        }
    }

    fun getSalesByPeriod(): Flow<List<SalesByPeriod>> =
        saleArchiveDao.getSalesByPeriod()
}