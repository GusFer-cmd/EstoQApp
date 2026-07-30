package com.example.estoq.data.Dao.PivotSaleItem

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.estoq.data.Model.PivotSaleItem.PivotSaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PivotSaleItemDao {

    @Query("SELECT * FROM PivotSaleItem WHERE saleArchiveId = :saleArchiveId")
    suspend fun getItemsBySaleArchiveId(saleArchiveId: Long): List<PivotSaleItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<PivotSaleItem>)

    @Query("DELETE FROM PivotSaleItem WHERE saleArchiveId = :saleArchiveId")
    suspend fun deleteBySaleArchiveId(saleArchiveId: Long)
}
