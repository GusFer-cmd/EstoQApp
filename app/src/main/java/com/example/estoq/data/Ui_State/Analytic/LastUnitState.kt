package com.example.estoq.data.Ui_State.Analytic

import com.example.estoq.data.Model.Item.Item

data class LastUnitUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isRestocked: Boolean = false,
    val error: String? = null
)
