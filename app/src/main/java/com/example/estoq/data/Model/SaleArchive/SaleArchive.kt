package com.example.estoq.data.Model.SaleArchive

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaleArchive(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val paymentMethod: SaleArchivePaymentMethod = SaleArchivePaymentMethod.CASH,
    val installment: String = "",
    val totalValue: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
