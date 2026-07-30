package com.example.estoq.data.Exceptions.SaleArchive

sealed class SaleArchiveException(message: String) : Exception(message) {

    class SaleArchiveNotFoundException : SaleArchiveException("Venda não encontrada")

    class InvalidIdException : SaleArchiveException("Venda inválida")

    class EmptyClientIdException : SaleArchiveException("Campo cliente é obrigatório")

    class EmptyInstallmenteException : SaleArchiveException("Campo parcela é obrigatório")

    class EmptyCartException : SaleArchiveException("Adicione itens ao carrinho")

    class SaleArchiveUnknownException : SaleArchiveException("Ocorreu um erro inesperado")
}