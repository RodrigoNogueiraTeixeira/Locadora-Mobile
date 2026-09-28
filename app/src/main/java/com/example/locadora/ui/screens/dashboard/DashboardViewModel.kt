package com.example.locadora.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.locadora.data.local.model.LocacaoCompleta
import com.example.locadora.data.repository.LocacaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pelo Dashboard de Locações Ativas (RF01).
 */
class DashboardViewModel(
    private val locacaoRepository: LocacaoRepository
) : ViewModel() {

    // RF01.2: Lista apenas as locações com status ATIVA observadas reativamente do Room
    val locacoesAtivas: StateFlow<List<LocacaoCompleta>> =
        locacaoRepository.locacoesAtivas
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _mensagemFeedback = MutableStateFlow<String?>(null)
    val mensagemFeedback: StateFlow<String?> = _mensagemFeedback.asStateFlow()

    fun limparFeedback() {
        _mensagemFeedback.value = null
    }

    /**
     * Finaliza a locação selecionada e devolve o veículo à frota como DISPONÍVEL.
     */
    fun finalizarLocacao(locacao: LocacaoCompleta) {
        viewModelScope.launch {
            try {
                locacaoRepository.finalizarLocacao(
                    locacaoId = locacao.locacao.id,
                    veiculoId = locacao.veiculo.id
                )
                _mensagemFeedback.value = "Locação finalizada com sucesso! Veículo liberado."
            } catch (e: Exception) {
                _mensagemFeedback.value = "Erro ao finalizar locação: ${e.localizedMessage}"
            }
        }
    }
}

/**
 * Factory para instanciar o DashboardViewModel.
 */
class DashboardViewModelFactory(
    private val locacaoRepository: LocacaoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(locacaoRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
