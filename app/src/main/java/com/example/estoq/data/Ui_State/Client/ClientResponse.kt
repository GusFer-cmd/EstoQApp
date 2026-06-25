package com.example.estoq.data.Ui_State.Client

sealed interface ClientResponse {
    data object Success : ClientResponse
    data class Error(val errorMessage: String) : ClientResponse
}