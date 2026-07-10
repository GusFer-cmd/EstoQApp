package com.example.estoq.data.Dao.Item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.estoq.data.Model.Item.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM Item ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Item>>

    @Query("SELECT * FROM Item WHERE (name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%') AND (:type IS NULL OR type = :type) ORDER BY createdAt DESC")
    fun searchByNameOrBrandAndType(query: String, type: String?): Flow<List<Item>>

    @Query("SELECT * FROM Item WHERE id = :id")
    suspend fun getById(id: Long): Item?

    @Query("SELECT * FROM Item WHERE storageId = :storageId ORDER BY createdAt DESC")
    fun getByStorageId(storageId: Long): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Query("DELETE FROM Item WHERE id = :id")
    suspend fun delete(id: Long)
}
