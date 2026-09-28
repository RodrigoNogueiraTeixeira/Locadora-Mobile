package com.example.locadora

import android.app.Application
import com.example.locadora.data.local.AppDatabase
import com.example.locadora.data.repository.ClienteRepository
import com.example.locadora.data.repository.LocacaoRepository
import com.example.locadora.data.repository.VeiculoRepository

/**
 * Classe Application do aplicativo para inicialização dos repositórios e banco de dados.
 */
class LocadoraApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val veiculoRepository by lazy { VeiculoRepository(database.veiculoDao()) }

    val clienteRepository by lazy { ClienteRepository(database.clienteDao()) }

    val locacaoRepository by lazy {
        LocacaoRepository(
            locacaoDao = database.locacaoDao(),
            veiculoDao = database.veiculoDao()
        )
    }
}
