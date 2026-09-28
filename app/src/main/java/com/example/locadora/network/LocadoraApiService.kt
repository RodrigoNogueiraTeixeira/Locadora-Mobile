package com.example.locadora.network

import com.example.locadora.network.dto.SyncResponseDto
import com.example.locadora.network.dto.VeiculoDto
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interface Retrofit com as definições de endpoints REST para sincronização.
 */
interface LocadoraApiService {

    @GET("api/v1/veiculos")
    suspend fun getVeiculosRemotos(): List<VeiculoDto>

    @POST("api/v1/locacoes/sincronizar")
    suspend fun sincronizarDados(): SyncResponseDto
}
