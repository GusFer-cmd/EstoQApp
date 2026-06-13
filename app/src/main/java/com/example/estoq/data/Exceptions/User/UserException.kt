package com.example.estoq.data.Exceptions.User

sealed class UserException(message: String) : Exception(message) {

    class UserNotFoundException(id: String) : UserException("Usuário não encontrado!.")

    class UserUnknownException : UserException("Ocorreu um erro inesperado!.")
}