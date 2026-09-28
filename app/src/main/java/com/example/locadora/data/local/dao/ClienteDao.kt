package com.example.locadora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.locadora.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO para operações na tabela de clientes.
 */
@Dao
interface ClienteDao {

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun getAllClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id LIMIT 1")
    suspend fun getClienteById(id: Long): ClienteEntity?

    @Query("SELECT * FROM clientes WHERE contactId = :contactId LIMIT 1")
    suspend fun getClienteByContactId(contactId: String): ClienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity): Long
}
