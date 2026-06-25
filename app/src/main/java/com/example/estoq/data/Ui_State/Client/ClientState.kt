package com.example.estoq.data.Ui_State.Client

data class ClientUiState(
    val id: Long = 0,
    val firstName: String = "",
    val firstNameError: String? = null,
    val secondName: String = "",
    val secondNameError: String? = null,
    val telephone: String = "",
    val telephoneError: String? = null,
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)
