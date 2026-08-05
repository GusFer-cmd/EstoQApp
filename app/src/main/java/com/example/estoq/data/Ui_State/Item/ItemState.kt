package com.example.estoq.data.Ui_State.Item

import com.example.estoq.data.Model.Item.ItemType

data class ItemUiState(
    val id: Long = 0,

    val name: String = "",
    val nameError: String? = null,

    val brand: String = "",
    val brandError: String? = null,

    val currentPrice: String = "",
    val currentPriceError: String? = null,

    val costPrice: String = "",
    val costPriceError: String? = null,

    val stockQuantity: String = "",
    val stockQuantityError: String? = null,

    val imagePath: String? = null,

    val storageId: Long = 0,
    val storageIdError: String? = null,

    val type: ItemType = ItemType.BLUSA,
    val typeError: String? = null,

    val size: String = "",
    val sizeError: String? = null,

    val color: String = "",
    val colorError: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null
)
