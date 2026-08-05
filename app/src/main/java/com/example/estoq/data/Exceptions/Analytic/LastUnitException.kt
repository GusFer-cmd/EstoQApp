package com.example.estoq.data.Exceptions.Analytic

sealed class LastUnitException(message: String) : Exception(message) {
    class NotFoundException : LastUnitException("Nenhum item encontrado")
    class LastUnitUnknownException : LastUnitException("Ocorreu um erro inesperado")
}
