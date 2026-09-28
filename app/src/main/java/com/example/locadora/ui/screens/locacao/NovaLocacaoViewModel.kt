package com.example.locadora.ui.screens.locacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.model.ContatoDispositivo
import com.example.locadora.data.repository.ClienteRepository
import com.example.locadora.data.repository.LocacaoRepository
import com.example.locadora.data.repository.VeiculoRepository
import com.example.locadora.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel responsável pelo fluxo de abertura de uma nova locação (RF04).
 */
class NovaLocacaoViewModel(
    private val veiculoRepository: VeiculoRepository,
    private val clienteRepository: ClienteRepository,
    private val locacaoRepository: LocacaoRepository
) : ViewModel() {

    // RF04.1: Apenas veículos DISPONÍVEIS
    val veiculosDisponiveis: StateFlow<List<VeiculoEntity>> =
        veiculoRepository.veiculosDisponiveis
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _veiculoSelecionado = MutableStateFlow<VeiculoEntity?>(null)
    val veiculoSelecionado: StateFlow<VeiculoEntity?> = _veiculoSelecionado.asStateFlow()

    private val _clienteSelecionado = MutableStateFlow<ContatoDispositivo?>(null)
    val clienteSelecionado: StateFlow<ContatoDispositivo?> = _clienteSelecionado.asStateFlow()

    // Datas padrão: saída hoje, entrega amanhã
    private val hojeMillis = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val amanhaMillis = hojeMillis + (24 * 60 * 60 * 1000)

    private val _dataSaidaMillis = MutableStateFlow(hojeMillis)
    val dataSaidaMillis: StateFlow<Long> = _dataSaidaMillis.asStateFlow()

    private val _dataEntregaMillis = MutableStateFlow(amanhaMillis)
    val dataEntregaMillis: StateFlow<Long> = _dataEntregaMillis.asStateFlow()

    private val _quantidadeDias = MutableStateFlow(1L)
    val quantidadeDias: StateFlow<Long> = _quantidadeDias.asStateFlow()

    private val _valorTotalEstimado = MutableStateFlow(0.0)
    val valorTotalEstimado: StateFlow<Double> = _valorTotalEstimado.asStateFlow()

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

    private val _locacaoSalvaSucesso = MutableStateFlow(false)
    val locacaoSalvaSucesso: StateFlow<Boolean> = _locacaoSalvaSucesso.asStateFlow()

    fun selecionarVeiculo(veiculo: VeiculoEntity) {
        _veiculoSelecionado.value = veiculo
        recalcularTotal()
    }

    fun selecionarCliente(contato: ContatoDispositivo) {
        _clienteSelecionado.value = contato
    }

    fun setDataSaida(millis: Long) {
        _dataSaidaMillis.value = millis
        // Se a data de entrega for anterior à saída, ajusta a data de entrega
        if (_dataEntregaMillis.value < millis) {
            _dataEntregaMillis.value = millis
        }
        recalcularTotal()
    }

    fun setDataEntrega(millis: Long) {
        _dataEntregaMillis.value = millis
        recalcularTotal()
    }

    fun limparMensagemErro() {
        _mensagemErro.value = null
    }

    fun resetarSucesso() {
        _locacaoSalvaSucesso.value = false
    }

    /**
     * RF04.4: Calcula o valor total estimado (Dias x Diária).
     */
    private fun recalcularTotal() {
        val dias = DateUtils.calcularQuantidadeDias(
            dataSaidaMillis = _dataSaidaMillis.value,
            dataEntregaMillis = _dataEntregaMillis.value
        )
        _quantidadeDias.value = dias

        val diaria = _veiculoSelecionado.value?.valorDiaria ?: 0.0
        _valorTotalEstimado.value = dias * diaria
    }

    /**
     * RF04.5: Valida e confirma a locação, salvando no Room e alterando status do veículo.
     */
    fun confirmarLocacao() {
        val veiculo = _veiculoSelecionado.value
        if (veiculo == null) {
            _mensagemErro.value = "Por favor, selecione um veículo disponível."
            return
        }

        val cliente = _clienteSelecionado.value
        if (cliente == null) {
            _mensagemErro.value = "Por favor, selecione um cliente da agenda de contatos."
            return
        }

        if (_dataEntregaMillis.value < _dataSaidaMillis.value) {
            _mensagemErro.value = "A data de entrega não pode ser anterior à data de saída."
            return
        }

        viewModelScope.launch {
            try {
                // Salva ou recupera o cliente no banco local
                val clienteId = clienteRepository.salvarOuRecuperarCliente(
                    contactId = cliente.id,
                    nome = cliente.nome,
                    telefone = cliente.telefone
                )

                // Cria a locação e atualiza o veículo para ALUGADO
                locacaoRepository.criarLocacao(
                    veiculoId = veiculo.id,
                    clienteId = clienteId,
                    dataSaida = _dataSaidaMillis.value,
                    dataPrevistaEntrega = _dataEntregaMillis.value,
                    valorTotal = _valorTotalEstimado.value
                )

                _mensagemErro.value = null
                _locacaoSalvaSucesso.value = true
            } catch (e: Exception) {
                _mensagemErro.value = "Erro ao salvar a locação: ${e.localizedMessage}"
            }
        }
    }
}

/**
 * Factory para instanciar o NovaLocacaoViewModel.
 */
class NovaLocacaoViewModelFactory(
    private val veiculoRepository: VeiculoRepository,
    private val clienteRepository: ClienteRepository,
    private val locacaoRepository: LocacaoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NovaLocacaoViewModel::class.java)) {
            return NovaLocacaoViewModel(veiculoRepository, clienteRepository, locacaoRepository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
