package com.example.estoq.data.Exceptions.Client

sealed class ClientException(message: String) : Exception(message) {
    class EmptyFirstName : ClientException("O campo nome é obrigatório")
    class EmptyLastName : ClientException("O campo sobrenome é obrigatório")
    class EmptyTelephone : ClientException("O campo telefone é obrigatório")
    class EmptyCep : ClientException("O campo CEP é obrigatório")
    class InvalidCep : ClientException("CEP inválido ou não encontrado")
    class EmptyNumero : ClientException("O campo número é obrigatório")
    class InvalidIdException : ClientException("Cliente inválido")
    class ClientUnknownException : ClientException("Ocorreu um erro inesperado")
}