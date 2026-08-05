package com.example.estoq.data.Ui_State.Client

data class ClientUiState(
    val id: Long = 0,

    val firstName: String = "",
    val firstNameError: String? = null,

    val lastName: String = "",
    val lastNameError: String? = null,

    val telephone: String = "",
    val telephoneError: String? = null,

    val cep: String = "",
    val cepError: String? = null,

    val logradouro: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val estado: String = "",

    val numero: String = "",
    val numeroError: String? = null,

    val isAddressLoading: Boolean = false,
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)
