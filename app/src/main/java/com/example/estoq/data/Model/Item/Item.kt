package com.example.estoq.data.Model.Item

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val currentPrice: Double,
    val stockQuantity: Int,
    val imagePath: String? = null,
    val storageId: Long,
    val type: ItemType = ItemType.BLUSA,
    val size: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
