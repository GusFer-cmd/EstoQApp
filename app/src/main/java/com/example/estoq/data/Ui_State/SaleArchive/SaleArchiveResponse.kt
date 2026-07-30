package com.example.estoq.data.Ui_State.SaleArchive

sealed interface SaleArchiveResponse {
    data object Success : SaleArchiveResponse
    data class Error(val errorMessage: String) : SaleArchiveResponse
}