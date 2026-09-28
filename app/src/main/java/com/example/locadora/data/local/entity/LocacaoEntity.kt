package com.example.locadora.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.locadora.data.local.model.StatusLocacao

/**
 * Tabela que armazena os registros de locação.
 * Utiliza chaves estrangeiras apontando para as tabelas de Veículos e Clientes.
 */
@Entity(
    tableName = "locacoes",
    foreignKeys = [
        ForeignKey(
            entity = VeiculoEntity::class,
            parentColumns = ["id"],
            childColumns = ["veiculoId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["veiculoId"]),
        Index(value = ["clienteId"])
    ]
)
data class LocacaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val veiculoId: Long,
    val clienteId: Long,
    val dataSaida: Long,
    val dataPrevistaEntrega: Long,
    val valorTotal: Double,
    val status: StatusLocacao = StatusLocacao.ATIVA
)
