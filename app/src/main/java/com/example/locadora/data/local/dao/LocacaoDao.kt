package com.example.locadora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.locadora.data.local.entity.LocacaoEntity
import com.example.locadora.data.local.model.LocacaoCompleta
import com.example.locadora.data.local.model.StatusLocacao
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO para operações na tabela de locações.
 * Utiliza @Transaction para consultas que retornam objetos com @Relation.
 */
@Dao
interface LocacaoDao {

    @Transaction
    @Query("SELECT * FROM locacoes WHERE status = :status ORDER BY dataPrevistaEntrega ASC")
    fun getLocacoesCompletasByStatus(status: StatusLocacao = StatusLocacao.ATIVA): Flow<List<LocacaoCompleta>>

    @Transaction
    @Query("SELECT * FROM locacoes ORDER BY dataSaida DESC")
    fun getAllLocacoesCompletas(): Flow<List<LocacaoCompleta>>

    @Transaction
    @Query("SELECT * FROM locacoes WHERE id = :id LIMIT 1")
    suspend fun getLocacaoCompletaById(id: Long): LocacaoCompleta?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocacao(locacao: LocacaoEntity): Long

    @Query("UPDATE locacoes SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: StatusLocacao): Int
}
