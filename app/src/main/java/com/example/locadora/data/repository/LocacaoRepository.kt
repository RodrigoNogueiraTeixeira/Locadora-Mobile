package com.example.locadora.data.repository

import com.example.locadora.data.local.dao.LocacaoDao
import com.example.locadora.data.local.dao.VeiculoDao
import com.example.locadora.data.local.entity.LocacaoEntity
import com.example.locadora.data.local.model.LocacaoCompleta
import com.example.locadora.data.local.model.StatusLocacao
import com.example.locadora.data.local.model.StatusVeiculo
import kotlinx.coroutines.flow.Flow

/**
 * Repositório responsável pelas regras e operações de locação de veículos.
 */
class LocacaoRepository(
    private val locacaoDao: LocacaoDao,
    private val veiculoDao: VeiculoDao
) {

    val locacoesAtivas: Flow<List<LocacaoCompleta>> =
        locacaoDao.getLocacoesCompletasByStatus(StatusLocacao.ATIVA)

    val allLocacoes: Flow<List<LocacaoCompleta>> =
        locacaoDao.getAllLocacoesCompletas()

    suspend fun getLocacaoCompletaById(id: Long): LocacaoCompleta? {
        return locacaoDao.getLocacaoCompletaById(id)
    }

    /**
     * Cria uma nova locação e atualiza o status do veículo para ALUGADO.
     */
    suspend fun criarLocacao(
        veiculoId: Long,
        clienteId: Long,
        dataSaida: Long,
        dataPrevistaEntrega: Long,
        valorTotal: Double
    ): Long {
        val novaLocacao = LocacaoEntity(
            veiculoId = veiculoId,
            clienteId = clienteId,
            dataSaida = dataSaida,
            dataPrevistaEntrega = dataPrevistaEntrega,
            valorTotal = valorTotal,
            status = StatusLocacao.ATIVA
        )
        val idLocacao = locacaoDao.insertLocacao(novaLocacao)

        // RF04.5: O status do veículo deve ser alterado automaticamente para ALUGADO
        veiculoDao.updateStatus(veiculoId, StatusVeiculo.ALUGADO)

        return idLocacao
    }

    /**
     * Finaliza uma locação ativa e libera o veículo (retorna para DISPONIVEL).
     */
    suspend fun finalizarLocacao(locacaoId: Long, veiculoId: Long) {
        locacaoDao.updateStatus(locacaoId, StatusLocacao.FINALIZADA)
        veiculoDao.updateStatus(veiculoId, StatusVeiculo.DISPONIVEL)
    }
}
