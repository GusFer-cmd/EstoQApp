package com.example.estoq.data.Dao.User

import androidx.room.Dao
import androidx.room.Query
import com.example.estoq.data.Model.User.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM User ORDER BY name ASC")
    fun getAll() : Flow<List<User>>

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getById(id: String) : User?

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getByEmail(email: String) : User?

    @Query("SELECT * FROM User WHERE name = :name")
    suspend fun getByName(name: String) : User?
}