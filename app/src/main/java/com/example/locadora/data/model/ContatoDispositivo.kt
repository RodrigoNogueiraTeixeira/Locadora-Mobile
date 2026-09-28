package com.example.locadora.data.model

/**
 * Modelo de dados que representa um contato lido da agenda do dispositivo (RF03).
 */
data class ContatoDispositivo(
    val id: String,
    val nome: String,
    val telefone: String
)
