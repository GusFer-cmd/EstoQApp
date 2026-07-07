package com.example.estoq.data.Repository.Client

import com.example.estoq.data.Dao.Client.ClientDao
import com.example.estoq.data.Exceptions.Client.ClientException
import com.example.estoq.data.Model.Client.Client
import kotlinx.coroutines.flow.Flow

class ClientRepository(private val clientDao: ClientDao) {

    fun getAll(): Flow<List<Client>> = clientDao.getAll()

    fun searchByName(query: String): Flow<List<Client>> =
        clientDao.searchByName(query)

    suspend fun getById(id: Long): Client? {
        return try {
            clientDao.getById(id)
        } catch (e: Exception) {
            throw ClientException.ClientUnknownException()
        }
    }

    suspend fun insert(client: Client): Long {
        return try {
            clientDao.insert(client)
        } catch (e: Exception) {
            throw ClientException.ClientUnknownException()
        }
    }

    suspend fun update(client: Client) {
        try {
            clientDao.update(client)
        } catch (e: Exception) {
            throw ClientException.ClientUnknownException()
        }
    }

    suspend fun delete(id: Long) {
        try {
            clientDao.delete(id)
        } catch (e: Exception) {
            throw ClientException.ClientUnknownException()
        }
    }
}