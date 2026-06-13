package com.example.estoq.data.Model.User

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

fun shortUuid() : String {
    return UUID.randomUUID().toString().substring(0, 8)
}

@Entity
data class User (
    @PrimaryKey(autoGenerate = false)
    val id: String = shortUuid(),
    val name: String,
    val email: String,
    val photoUrl: String?
)