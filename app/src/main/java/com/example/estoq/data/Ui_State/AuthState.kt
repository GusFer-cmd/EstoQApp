package com.example.estoq.data.Ui_State

interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val errorMessage: String, val errorCode: String? = null) : AuthResponse
}