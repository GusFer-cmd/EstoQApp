package com.example.estoq.data.Viewmodel.User

import androidx.lifecycle.ViewModel
import com.example.estoq.data.Exceptions.User.UserException
import com.example.estoq.data.Model.User.User
import com.example.estoq.data.Repository.User.UserRepository
import kotlinx.coroutines.flow.Flow

class UserViewModel (
    private val repository: UserRepository
) : ViewModel() {

    val userList: Flow<List<User>> = repository.getAll

    suspend fun getById(id: String) : User? {
        try {
            if(id.isEmpty()) {
                throw UserException.UserNotFoundException(id)
            }

            return repository.getById(id)
        } catch (e: UserException) {
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

            return repository.getByEmail(email)
        } catch (e: UserException) {
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

            return repository.getByName(name)
        } catch (e: UserException) {
            throw e
        } catch (e: Exception) {
            throw UserException.UserUnknownException()
        }
    }
}