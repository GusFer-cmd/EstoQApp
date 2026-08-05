package com.example.estoq.data.Dao.PivotSaleItem

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.estoq.data.Model.PivotSaleItem.ItemSalesSummary
import com.example.estoq.data.Model.PivotSaleItem.MonthlyProfitSummary
import com.example.estoq.data.Model.PivotSaleItem.PivotSaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PivotSaleItemDao {

    @Query("SELECT * FROM PivotSaleItem WHERE saleArchiveId = :saleArchiveId")
    suspend fun getItemsBySaleArchiveId(saleArchiveId: Long): List<PivotSaleItem>

    @Query(
        """
        SELECT i.id AS itemId, i.name AS name, i.brand AS brand, i.imagePath AS imagePath,
               i.currentPrice AS currentPrice, i.costPrice AS costPrice,
               i.size AS size, i.color AS color,
               i.stockQuantity AS stockQuantity, SUM(p.quantity) AS totalQuantity
        FROM PivotSaleItem p
        INNER JOIN Item i ON i.id = p.itemId
        GROUP BY i.id
        HAVING SUM(p.quantity) >= 3
        ORDER BY totalQuantity DESC
        """
    )
    fun getBestSellingItems(): Flow<List<ItemSalesSummary>>

    @Query(
        """
        SELECT i.id AS itemId, i.name AS name, i.brand AS brand, i.imagePath AS imagePath,
               i.currentPrice AS currentPrice, i.costPrice AS costPrice,
               i.size AS size, i.color AS color,
               i.stockQuantity AS stockQuantity, IFNULL(SUM(p.quantity), 0) AS totalQuantity
        FROM Item i
        LEFT JOIN PivotSaleItem p ON p.itemId = i.id
        GROUP BY i.id
        HAVING IFNULL(SUM(p.quantity), 0) <= 2
        ORDER BY totalQuantity ASC
        """
    )
    fun getLeastSellingItems(): Flow<List<ItemSalesSummary>>

    @Query(
        """
        SELECT strftime('%Y-%m', s.createdAt / 1000, 'unixepoch') AS monthKey,
               IFNULL(SUM((p.unitPrice - i.costPrice) * p.quantity), 0) AS profit,
               IFNULL(SUM(p.quantity), 0) AS totalQuantity,
               COUNT(DISTINCT s.id) AS totalSales
        FROM PivotSaleItem p
        INNER JOIN Item i ON i.id = p.itemId
        INNER JOIN SaleArchive s ON s.id = p.saleArchiveId
        GROUP BY monthKey
        ORDER BY monthKey DESC
        """
    )
    fun getProfitByMonth(): Flow<List<MonthlyProfitSummary>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<PivotSaleItem>)

    @Query("DELETE FROM PivotSaleItem WHERE saleArchiveId = :saleArchiveId")
    suspend fun deleteBySaleArchiveId(saleArchiveId: Long)
}
