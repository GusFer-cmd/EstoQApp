package com.example.estoq.data.Dao.Client

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.estoq.data.Model.Client.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM Client ORDER BY firstName ASC")
    fun getAll(): Flow<List<Client>>

    @Query("SELECT * FROM Client WHERE id = :id")
    suspend fun getById(id: Long): Client?

    @Query("SELECT * FROM Client WHERE firstName LIKE '%' || :query ORDER BY firstName ASC")
    fun searchByFirstName(query: String): Flow<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client): Long

    @Update
    suspend fun update(client: Client)

    @Delete
    suspend fun delete(client: Client)
}