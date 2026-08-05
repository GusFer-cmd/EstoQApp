package com.example.estoq.data.Ui_State.Analytic

import com.example.estoq.data.Model.Item.Item

sealed interface LastUnitResponse {
    data class Success(val items: List<Item>) : LastUnitResponse
    data class Error(val message: String?) : LastUnitResponse
}
