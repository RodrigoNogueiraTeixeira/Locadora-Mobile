package com.example.locadora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabela de cache de clientes selecionados a partir da agenda de contatos do dispositivo.
 */
@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactId: String,
    val nome: String,
    val telefone: String
)
