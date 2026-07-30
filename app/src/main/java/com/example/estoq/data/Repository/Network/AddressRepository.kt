package com.example.estoq.data.Repository.Network

import com.example.estoq.data.Exceptions.Client.ClientException
import com.example.estoq.data.Model.Network.AddressDto
import com.example.estoq.retrofit.AddressService

class AddressRepository(private val addressService: AddressService) {

    suspend fun findAddress(cep: String): AddressDto {
        val response = addressService.findAddress(cep)
        if (response.erro) {
            throw ClientException.InvalidCep()
        }
        return response
    }
}
