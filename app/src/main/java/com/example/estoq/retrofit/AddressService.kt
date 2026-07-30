package com.example.estoq.retrofit

import com.example.estoq.data.Model.Network.AddressDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AddressService {
    @GET("{cep}/json/")
    suspend fun findAddress(@Path("cep") cep: String): AddressDto
}