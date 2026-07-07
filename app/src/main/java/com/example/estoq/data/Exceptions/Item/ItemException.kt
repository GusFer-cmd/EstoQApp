package com.example.estoq.data.Exceptions.Item

sealed class ItemException(message: String) : Exception(message) {
    class EmptyNameException : ItemException("O campo nome é obrigatório")
    class EmptyBrandException : ItemException("O campo marca é obrigatório")
    class InvalidPriceException : ItemException("Preço inválido")
    class InvalidQuantityException : ItemException("Quantidade inválida")
    class EmptyTypeException : ItemException("Selecione um tipo de peça")
    class EmptySizeException : ItemException("Selecione um tamanho")
    class EmptyStorageException : ItemException("Selecione um estoque")
    class NotFoundException : ItemException("Não encontrado")
    class InvalidIdException : ItemException("Item inválido")
    class ItemUnknownException : ItemException("Ocorreu um erro inesperado")
}
