package com.example.locadora.network.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para resposta de sincronização.
 */
data class SyncResponseDto(
    @SerializedName("sucesso")
    val sucesso: Boolean,
    @SerializedName("mensagem")
    val mensagem: String,
    @SerializedName("totalSincronizados")
    val totalSincronizados: Int
)
