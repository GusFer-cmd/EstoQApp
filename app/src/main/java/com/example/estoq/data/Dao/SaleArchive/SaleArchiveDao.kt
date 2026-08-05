package com.example.estoq.data.Dao.SaleArchive

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.estoq.data.Model.SaleArchive.SaleArchive
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleArchiveDao {

    @Query("SELECT * FROM SaleArchive ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SaleArchive>>

    @Query("SELECT * FROM SaleArchive WHERE id = :id")
    suspend fun getById(id: Long): SaleArchive?

    @Query("SELECT * FROM SaleArchive WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getAllByClientId(clientId: Long): Flow<List<SaleArchive>>

    @Query("SELECT * FROM SaleArchive WHERE createdAt BETWEEN :startMillis AND :endMillis ORDER BY createdAt ASC")
    fun getSalesBetween(startMillis: Long, endMillis: Long): Flow<List<SaleArchive>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(saleArchive: SaleArchive): Long

    @Update
    suspend fun update(saleArchive: SaleArchive)

    @Query("DELETE FROM SaleArchive WHERE id = :id")
    suspend fun delete(id: Long)
}