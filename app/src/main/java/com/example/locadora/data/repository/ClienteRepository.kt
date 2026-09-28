package com.example.locadora.data.repository

import com.example.locadora.data.local.dao.ClienteDao
import com.example.locadora.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositório responsável pelo gerenciamento de clientes em cache local.
 */
class ClienteRepository(private val clienteDao: ClienteDao) {

    val allClientes: Flow<List<ClienteEntity>> = clienteDao.getAllClientes()

    suspend fun getClienteById(id: Long): ClienteEntity? {
        return clienteDao.getClienteById(id)
    }

    /**
     * Salva o cliente vindo dos contatos do celular caso ainda não esteja cadastrado,
     * ou retorna o ID caso já exista no banco.
     */
    suspend fun salvarOuRecuperarCliente(contactId: String, nome: String, telefone: String): Long {
        val existente = clienteDao.getClienteByContactId(contactId)
        return if (existente != null) {
            existente.id
        } else {
            val novoCliente = ClienteEntity(
                contactId = contactId,
                nome = nome,
                telefone = telefone
            )
            clienteDao.insertCliente(novoCliente)
        }
    }
}
