package com.example.estoq.data.Dao.Storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.estoq.data.Model.Storage.Storage
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageDao {

    @Query("SELECT * FROM Storage ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Storage>>

    @Query("SELECT * FROM Storage WHERE id = :id")
    suspend fun getById(id: Long): Storage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(storage: Storage): Long

    @Update
    suspend fun update(storage: Storage)

    @Delete
    suspend fun delete(storage: Storage)
}
