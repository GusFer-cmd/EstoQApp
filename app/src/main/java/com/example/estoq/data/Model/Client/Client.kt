package com.example.estoq.data.Model.Client

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val telephone: String,
)
