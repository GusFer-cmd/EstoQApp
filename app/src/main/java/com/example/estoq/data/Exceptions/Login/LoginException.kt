package com.example.estoq.data.Exceptions.Login

sealed class LoginException(message: String) : Exception(message) {

    class EmptyEmailException : LoginException("Campo e-mail é obrigatório")

    class EmptyPasswordException : LoginException("Campo senha é obrigatório")

    class ItemUnknownException : LoginException("Ocorreu um erro inesperado")

}