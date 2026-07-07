package com.example.estoq.data.Database

import androidx.room.TypeConverter
import com.example.estoq.data.Model.Item.ItemType

class Converters {
    @TypeConverter
    fun fromItemType(value: ItemType): String = value.code

    @TypeConverter
    fun toItemType(value: String): ItemType =
        ItemType.entries.firstOrNull { it.code == value } ?: ItemType.BLUSA
}
