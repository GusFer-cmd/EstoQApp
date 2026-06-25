package com.example.estoq.data.Ui_State.Storage

sealed interface StorageResponse {
    data object Success : StorageResponse
    data class Error(val errorMessage: String) : StorageResponse
}