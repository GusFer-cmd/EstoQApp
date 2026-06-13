package com.example.estoq.data.Viewmodel.User

import androidx.lifecycle.ViewModelProvider
import com.example.estoq.data.Repository.User.UserRepository

class UserViewModelFactory (
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModelFactory::class.java) -> {
                UserViewModelFactory(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }}