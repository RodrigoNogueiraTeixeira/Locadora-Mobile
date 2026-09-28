package com.example.locadora.ui.screens.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.local.model.StatusVeiculo
import com.example.locadora.data.repository.VeiculoRepository
import com.example.locadora.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela sincronização com a API REST (Retrofit 2).
 */
class SincronizacaoViewModel(
    private val veiculoRepository: VeiculoRepository
) : ViewModel() {

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _mensagemSucesso = MutableStateFlow<String?>(null)
    val mensagemSucesso: StateFlow<String?> = _mensagemSucesso.asStateFlow()

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

    private val _itensSincronizados = MutableStateFlow(0)
    val itensSincronizados: StateFlow<Int> = _itensSincronizados.asStateFlow()

    fun limparMensagens() {
        _mensagemSucesso.value = null
        _mensagemErro.value = null
    }

    /**
     * Consome o endpoint REST via Retrofit 2 e salva novos veículos na base local do Room.
     */
    fun sincronizarCatalogoVeiculos() {
        viewModelScope.launch {
            _carregando.value = true
            _mensagemSucesso.value = null
            _mensagemErro.value = null

            try {
                // Chamada de rede via Retrofit 2
                val veiculosRemotos = RetrofitClient.apiService.getVeiculosRemotos()
                val veiculosLocais = veiculoRepository.allVeiculos.first()
                val placasLocais = veiculosLocais.map { it.placa.uppercase() }.toSet()

                var novosInseridos = 0
                for (remoto in veiculosRemotos) {
                    if (!placasLocais.contains(remoto.placa.uppercase())) {
                        val novo = VeiculoEntity(
                            marca = remoto.marca,
                            modelo = remoto.modelo,
                            placa = remoto.placa,
                            ano = remoto.ano,
                            valorDiaria = remoto.valorDiaria,
                            status = StatusVeiculo.DISPONIVEL
                        )
                        veiculoRepository.inserirVeiculo(novo)
                        novosInseridos++
                    }
                }

                _itensSincronizados.value = novosInseridos
                _mensagemSucesso.value = "Sincronização concluída! $novosInseridos novos veículos importados da nuvem."
            } catch (e: Exception) {
                _mensagemErro.value = "Falha ao conectar com o serviço de sincronização: ${e.localizedMessage}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Envia os dados locais para backup na nuvem via endpoint POST do Retrofit 2.
     */
    fun sincronizarComNuvem() {
        viewModelScope.launch {
            _carregando.value = true
            _mensagemSucesso.value = null
            _mensagemErro.value = null

            try {
                val resposta = RetrofitClient.apiService.sincronizarDados()
                _mensagemSucesso.value = resposta.mensagem
            } catch (e: Exception) {
                _mensagemErro.value = "Erro na comunicação: ${e.localizedMessage}"
            } finally {
                _carregando.value = false
            }
        }
    }
}

/**
 * Factory para o SincronizacaoViewModel.
 */
class SincronizacaoViewModelFactory(
    private val veiculoRepository: VeiculoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SincronizacaoViewModel::class.java)) {
            return SincronizacaoViewModel(veiculoRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
