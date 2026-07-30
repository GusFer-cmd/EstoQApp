package com.example.estoq.data.Model.SaleArchive

enum class SaleArchivePaymentMethod(val code: String, val displayName: String) {
    CASH("CA", "Dinheiro"),
    CREDIT_CARD("CC", "Cartão de Crédito"),
    DEBIT_CARD("DC", "Cartão de Débito"),
    PIX("PX", "PIX"),
    ON_CREDIT("OC", "A prazo");


    fun availableInstallments(): List<String> = when (this) {
        CASH, DEBIT_CARD, PIX -> listOf("À vista")
        else -> listOf("1x", "2x", "3x", "4x", "5x", "6x", "7x", "8x", "9x", "10x", "11x", "12x")
    }

}