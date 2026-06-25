package com.example.estoq.data.Repository.Storage

import com.example.estoq.data.Dao.Storage.StorageDao
import com.example.estoq.data.Exceptions.Storage.StorageException
import com.example.estoq.data.Model.Storage.Storage
import kotlinx.coroutines.flow.Flow

class StorageRepository(private val storageDao: StorageDao) {

    fun getAll(): Flow<List<Storage>> = storageDao.getAll()

    suspend fun getById(id: Long): Storage? {
        return try {
            storageDao.getById(id)
        } catch (e: Exception) {
            throw StorageException.ItemUnknownException()
        }
    }

    suspend fun insert(storage: Storage): Long {
        return try {
            storageDao.insert(storage)
        } catch (e: Exception) {
            throw StorageException.ItemUnknownException()
        }
    }

    suspend fun update(storage: Storage) {
        try {
            storageDao.update(storage)
        } catch (e: Exception) {
            throw StorageException.ItemUnknownException()
        }
    }

    suspend fun delete(storage: Storage) {
        try {
            storageDao.delete(storage)
        } catch (e: Exception) {
            throw StorageException.ItemUnknownException()
        }
    }
}
