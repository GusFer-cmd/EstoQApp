package com.example.estoq.data.Ui_State.Storage

data class StorageUiState(
    val id: Long = 0,
    val title: String = "",
    val titleError: String? = null,

    val mainColor: Long = 0xFFFF69B4L,
    val hue: Float = 330f,
    val saturation: Float = 0.59f,
    val brightness: Float = 1f,
    val mainColorError: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)
