package com.example.estoq.data.Ui_State.Network

sealed interface AddressResponse {
    data object Success : AddressResponse
    data class Error(val errorMesage: String) : AddressResponse
}