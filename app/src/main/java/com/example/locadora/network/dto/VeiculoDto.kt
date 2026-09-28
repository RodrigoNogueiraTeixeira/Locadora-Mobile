package com.example.locadora.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para transferência de dados de veículos via Retrofit 2.
 */
data class VeiculoDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("marca")
    val marca: String,
    @SerializedName("modelo")
    val modelo: String,
    @SerializedName("placa")
    val placa: String,
    @SerializedName("ano")
    val ano: Int,
    @SerializedName("valorDiaria")
    val valorDiaria: Double,
    @SerializedName("status")
    val status: String
)
