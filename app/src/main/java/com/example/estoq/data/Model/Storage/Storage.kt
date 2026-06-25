package com.example.estoq.data.Model.Storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Storage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val mainColor: Long,
    val createdAt: Long = System.currentTimeMillis()
)
