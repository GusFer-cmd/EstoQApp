package com.example.estoq.data.Database

import androidx.room.TypeConverter
import com.example.estoq.data.Model.Item.ItemType
import com.example.estoq.data.Model.SaleArchive.SaleArchivePaymentMethod

class Converters {
    @TypeConverter
    fun fromItemType(value: ItemType): String = value.code

    @TypeConverter
    fun toItemType(value: String): ItemType =
        ItemType.entries.firstOrNull { it.code == value } ?: ItemType.BLUSA

    @TypeConverter
    fun fromSaleArchivePaymentMethod(value: SaleArchivePaymentMethod): String = value.code

    @TypeConverter
    fun toSaleArchivePaymentMethod(value: String): SaleArchivePaymentMethod =
        SaleArchivePaymentMethod.entries.firstOrNull { it.code == value } ?: SaleArchivePaymentMethod.CASH
}
