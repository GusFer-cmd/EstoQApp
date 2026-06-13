package com.example.estoq.data.Repository.User

import com.example.estoq.data.Dao.User.UserDao
import com.example.estoq.data.Exceptions.User.UserException
import com.example.estoq.data.Model.User.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val getAll: Flow<List<User>> = userDao.getAll()

    suspend fun getById(id: String) : User? {
        try {
            if(id.isEmpty()) {
                throw UserException.UserNotFoundException(id)
            }

            return userDao.getById(id)
        }
        catch (e: UserException) {
            throw e
        } catch (e: Exception) {
            throw UserException.UserUnknownException()
        }
    }

    suspend fun getByEmail(email: String) : User? {
        try {
            if(email.isEmpty()) {
                throw UserException.UserNotFoundException(email)
            }

            return userDao.getByEmail(email)
        }
        catch (e: UserException) {
            throw e
        } catch (e: Exception) {
            throw UserException.UserUnknownException()
        }
    }

    suspend fun getByName(name: String) : User? {
        try {
            if(name.isEmpty()) {
                throw UserException.UserNotFoundException(name)
            }

            return userDao.getByName(name)
        }
        catch (e: UserException) {
            throw e
        } catch (e: Exception) {
            throw UserException.UserUnknownException()
        }
    }
}