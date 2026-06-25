package com.example.estoq.data.Ui_State.Item

sealed interface ItemResponse {
    data object Success : ItemResponse
    data class Error(val errorMessage: String) : ItemResponse
}