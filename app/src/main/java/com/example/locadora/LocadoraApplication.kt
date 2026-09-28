package com.example.locadora

import android.app.Application
import com.example.locadora.data.local.AppDatabase
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.local.model.StatusVeiculo
import com.example.locadora.data.repository.ClienteRepository
import com.example.locadora.data.repository.LocacaoRepository
import com.example.locadora.data.repository.VeiculoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    override fun onCreate() {
        super.onCreate()

        // Inicializa automaticamente dados de exemplo se a frota estiver vazia (facilita testes e demonstração)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val veiculosAtuais = veiculoRepository.allVeiculos.first()
                if (veiculosAtuais.isEmpty()) {
                    veiculoRepository.inserirVeiculo(
                        VeiculoEntity(
                            marca = "Fiat",
                            modelo = "Mobi Like",
                            placa = "ABC1D23",
                            ano = 2023,
                            valorDiaria = 110.0,
                            status = StatusVeiculo.DISPONIVEL
                        )
                    )
                    veiculoRepository.inserirVeiculo(
                        VeiculoEntity(
                            marca = "Toyota",
                            modelo = "Corolla XEi",
                            placa = "BRA2E19",
                            ano = 2024,
                            valorDiaria = 220.0,
                            status = StatusVeiculo.DISPONIVEL
                        )
                    )
                    veiculoRepository.inserirVeiculo(
                        VeiculoEntity(
                            marca = "Jeep",
                            modelo = "Renegade Longitude",
                            placa = "JEP4A44",
                            ano = 2023,
                            valorDiaria = 190.0,
                            status = StatusVeiculo.DISPONIVEL
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
