package com.example.estoq.data.Model.PivotSaleItem

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.estoq.data.Model.Item.Item
import com.example.estoq.data.Model.SaleArchive.SaleArchive

@Entity(
    primaryKeys = ["saleArchiveId", "itemId"],
    foreignKeys = [
        ForeignKey(
            entity = SaleArchive::class,
            parentColumns = ["id"],
            childColumns = ["saleArchiveId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PivotSaleItem(
    val saleArchiveId: Long,
    val itemId: Long,
    val quantity: Int,
    val unitPrice: Double
)
