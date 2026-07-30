package com.example.estoq.data.Repository.Item

import com.example.estoq.data.Dao.Item.ItemDao
import com.example.estoq.data.Exceptions.Item.ItemException
import com.example.estoq.data.Model.Item.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {

    fun getAll(): Flow<List<Item>> = itemDao.getAll()

    fun getByStorageId(storageId: Long): Flow<List<Item>> =
        itemDao.getByStorageId(storageId)

    fun searchByNameOrBrandAndType(query: String, type: String?): Flow<List<Item>> =
        itemDao.searchByNameOrBrandAndType(query, type)

    suspend fun getById(id: Long): Item? {
        return try {
            itemDao.getById(id)
        } catch (e: Exception) {
            throw ItemException.ItemUnknownException()
        }
    }

    suspend fun insert(item: Item) {
        return try {
            itemDao.insert(item)
        } catch (e: Exception) {
            throw ItemException.ItemUnknownException()
        }
    }

    suspend fun update(item: Item) {
        try {
            itemDao.update(item)
        } catch (e: Exception) {
            throw ItemException.ItemUnknownException()
        }
    }

    suspend fun delete(id: Long) {
        try {
            itemDao.delete(id)
        } catch (e: Exception) {
            throw ItemException.ItemUnknownException()
        }
    }

    suspend fun decrementStock(id: Long, quantity: Int) {
        try {
            itemDao.decrementStock(id, quantity)
        } catch (e: Exception) {
            throw ItemException.ItemUnknownException()
        }
    }

    suspend fun incrementStock(id: Long, quantity: Int) {
        try {
            itemDao.incrementStock(id, quantity)
        } catch (e: Exception) {
            throw ItemException.ItemUnknownException()
        }
    }

    fun getLowStockItems(threshold: Int): Flow<List<Item>> =
        itemDao.getLowStockItems(threshold)
}
