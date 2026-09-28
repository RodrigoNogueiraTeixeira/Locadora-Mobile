package com.example.locadora.data.repository

import com.example.locadora.data.local.dao.VeiculoDao
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.local.model.StatusVeiculo
import kotlinx.coroutines.flow.Flow

/**
 * Repositório responsável por intermediar os dados de veículos entre o banco e a camada de UI.
 */
class VeiculoRepository(private val veiculoDao: VeiculoDao) {

    val allVeiculos: Flow<List<VeiculoEntity>> = veiculoDao.getAllVeiculos()

    val veiculosDisponiveis: Flow<List<VeiculoEntity>> = veiculoDao.getVeiculosByStatus(StatusVeiculo.DISPONIVEL)

    suspend fun getVeiculoById(id: Long): VeiculoEntity? {
        return veiculoDao.getVeiculoById(id)
    }

    suspend fun inserirVeiculo(veiculo: VeiculoEntity): Long {
        return veiculoDao.insertVeiculo(veiculo)
    }

    suspend fun atualizarStatus(id: Long, status: StatusVeiculo) {
        veiculoDao.updateStatus(id, status)
    }

    suspend fun deletarVeiculo(veiculo: VeiculoEntity) {
        veiculoDao.deleteVeiculo(veiculo)
    }
}
