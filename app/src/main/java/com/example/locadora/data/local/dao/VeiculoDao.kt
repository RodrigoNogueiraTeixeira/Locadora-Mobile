package com.example.locadora.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.local.model.StatusVeiculo
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO para operações na tabela de veículos.
 */
@Dao
interface VeiculoDao {

    @Query("SELECT * FROM veiculos ORDER BY modelo ASC")
    fun getAllVeiculos(): Flow<List<VeiculoEntity>>

    @Query("SELECT * FROM veiculos WHERE status = :status ORDER BY modelo ASC")
    fun getVeiculosByStatus(status: StatusVeiculo = StatusVeiculo.DISPONIVEL): Flow<List<VeiculoEntity>>

    @Query("SELECT * FROM veiculos WHERE id = :id LIMIT 1")
    suspend fun getVeiculoById(id: Long): VeiculoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVeiculo(veiculo: VeiculoEntity): Long

    @Update
    suspend fun updateVeiculo(veiculo: VeiculoEntity): Int

    @Query("UPDATE veiculos SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: StatusVeiculo): Int

    @Delete
    suspend fun deleteVeiculo(veiculo: VeiculoEntity): Int
}
