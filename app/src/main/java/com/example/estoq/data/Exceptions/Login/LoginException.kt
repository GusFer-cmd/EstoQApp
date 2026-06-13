package com.example.estoq.data.Exceptions.Login

sealed class LoginException(message: String) : Exception(message) {

    class WrongCredentialsException : LoginException("Credenciais inválidas")
    class EmptyEmailException : LoginException("Campo e-mail é obrigatório")
    class EmptyPasswordException : LoginException("Campo senha é obrigatório")
    class InvalidEmailException : LoginException("Formato de e-mail inválido")
    class PasswordMismatchException : LoginException("As senhas não coincidem")
    class EmailAlreadyInUseException : LoginException("Este e-mail já está vinculado a outra conta")
    class ItemUnknownException : LoginException("Ocorreu um erro inesperado")

}