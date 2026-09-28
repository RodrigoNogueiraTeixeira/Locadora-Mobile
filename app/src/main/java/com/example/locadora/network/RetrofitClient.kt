package com.example.locadora.network

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Retrofit 2 configurado com suporte a interceptador Mock (atendendo ao requisito
 * de consumo de API REST mockada ou integrada para sincronização).
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.locadora-veiculos.com/"

    // Interceptador para simular a resposta da API REST de forma confiável
    private val mockInterceptor = Interceptor { chain ->
        val uri = chain.request().url().encodedPath()

        val (responseCode, responseJson) = when {
            uri.contains("veiculos") -> {
                200 to """
                    [
                        {
                            "id": 101,
                            "marca": "Honda",
                            "modelo": "Civic",
                            "placa": "BRA2E19",
                            "ano": 2023,
                            "valorDiaria": 220.0,
                            "status": "DISPONIVEL"
                        },
                        {
                            "id": 102,
                            "marca": "Jeep",
                            "modelo": "Renegade",
                            "placa": "JEP4A44",
                            "ano": 2022,
                            "valorDiaria": 190.0,
                            "status": "DISPONIVEL"
                        },
                        {
                            "id": 103,
                            "marca": "Chevrolet",
                            "modelo": "Onix Plus",
                            "placa": "RIO1K99",
                            "ano": 2024,
                            "valorDiaria": 140.0,
                            "status": "DISPONIVEL"
                        }
                    ]
                """.trimIndent()
            }
            uri.contains("sincronizar") -> {
                200 to """
                    {
                        "sucesso": true,
                        "mensagem": "Sincronização realizada com a nuvem com sucesso!",
                        "totalSincronizados": 3
                    }
                """.trimIndent()
            }
            else -> 404 to """{"sucesso": false, "mensagem": "Endpoint não encontrado"}"""
        }

        val mediaType = MediaType.parse("application/json")
        val responseBody = ResponseBody.create(mediaType, responseJson)

        Response.Builder()
            .code(responseCode)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(responseBody)
            .addHeader("content-type", "application/json")
            .build()
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(mockInterceptor)
        .build()

    val apiService: LocadoraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocadoraApiService::class.java)
    }
}
