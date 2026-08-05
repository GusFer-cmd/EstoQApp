package com.example.estoq.data.Model.PivotSaleItem

data class ItemSalesSummary(
    val itemId: Long,
    val name: String,
    val brand: String,
    val imagePath: String? = null,
    val currentPrice: Double,
    val costPrice: Double = 0.0,
    val size: String = "",
    val color: String = "",
    val stockQuantity: Int,
    val totalQuantity: Int
)

data class MonthlyProfitSummary(
    val monthKey: String,
    val profit: Double,
    val totalQuantity: Int,
    val totalSales: Int
)
