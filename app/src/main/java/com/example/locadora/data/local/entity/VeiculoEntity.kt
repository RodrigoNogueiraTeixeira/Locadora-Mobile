package com.example.locadora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.locadora.data.local.model.StatusVeiculo

/**
 * Tabela que armazena os veículos cadastrados no sistema.
 */
@Entity(tableName = "veiculos")
data class VeiculoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val marca: String,
    val modelo: String,
    val placa: String,
    val ano: Int,
    val valorDiaria: Double,
    val status: StatusVeiculo = StatusVeiculo.DISPONIVEL
)
