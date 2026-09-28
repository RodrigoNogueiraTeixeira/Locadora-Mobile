package com.example.locadora.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.locadora.data.local.entity.ClienteEntity
import com.example.locadora.data.local.entity.LocacaoEntity
import com.example.locadora.data.local.entity.VeiculoEntity

/**
 * POJO que combina a locação com os dados relacionados do Veículo e do Cliente
 * através da anotação @Relation do Room.
 */
data class LocacaoCompleta(
    @Embedded
    val locacao: LocacaoEntity,

    @Relation(
        parentColumn = "veiculoId",
        entityColumn = "id"
    )
    val veiculo: VeiculoEntity,

    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: ClienteEntity
)
