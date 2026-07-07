package com.example.estoq.data.Dao.Client

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.estoq.data.Model.Client.Client
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM Client ORDER BY firstName || ' ' || lastName ASC")
    fun getAll(): Flow<List<Client>>

    @Query("SELECT * FROM Client WHERE id = :id")
    suspend fun getById(id: Long): Client?

    @Query("SELECT * FROM Client WHERE firstName || ' ' || lastName LIKE '%' || :query || '%' ORDER BY firstName || ' ' || lastName ASC")
    fun searchByName(query: String): Flow<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client): Long

    @Update
    suspend fun update(client: Client)

    @Query("DELETE FROM Client WHERE id = :id")
    suspend fun delete(id: Long)
}