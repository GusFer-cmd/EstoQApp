package com.example.estoq.data.Ui_State.SaleArchive

import com.example.estoq.data.Model.SaleArchive.SaleArchivePaymentMethod

data class CartUiItem(
    val itemId: Long,
    val name: String,
    val brand: String,
    val size: String,
    val quantity: Int,
    val unitPrice: Double,
    val stockQuantity: Int,
    val subtotal: Double = quantity * unitPrice
)

data class DetailSaleItem(
    val name: String,
    val brand: String,
    val size: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double = quantity * unitPrice
)

data class SaleArchiveUiState(
    val id: Long = 0,

    val clientId: Long = 0,
    val clientIdError: String? = null,

    val paymentMethod: SaleArchivePaymentMethod = SaleArchivePaymentMethod.CASH,
    val paymentMethodError: String? = null,

    val installment: String = "",
    val installmentError: String? = null,

    val totalValue: Double = 0.0,

    val cartItems: List<CartUiItem> = emptyList(),

    val currentStep: Int = 1,

    val createdAt: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null,
    val clientSearchQuery: String = "",
    val detailClientName: String = "",
    val detailItems: List<DetailSaleItem> = emptyList()
)