package com.example.estoq.data.Exceptions.Storage

sealed class StorageException(message: String) : Exception(message) {

    class EmptyTitleException : StorageException("O campo título é obrigatório")

    class EmptyColorException : StorageException("Selecione uma cor")
    class NotFoundException : StorageException("Não encontrado")
    class InvalidIdException : StorageException("Estoque inválido")
    class ItemUnknownException : StorageException("Ocorreu um erro inesperado")
}
