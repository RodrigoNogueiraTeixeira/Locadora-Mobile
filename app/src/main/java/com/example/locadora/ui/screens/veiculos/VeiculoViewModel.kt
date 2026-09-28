package com.example.locadora.ui.screens.veiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.local.model.StatusVeiculo
import com.example.locadora.data.repository.VeiculoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela gestão da frota de veículos (RF02).
 */
class VeiculoViewModel(private val repository: VeiculoRepository) : ViewModel() {

    // Lista de veículos observada diretamente do Room
    val veiculos: StateFlow<List<VeiculoEntity>> = repository.allVeiculos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estados do formulário de cadastro
    private val _marca = MutableStateFlow("")
    val marca: StateFlow<String> = _marca.asStateFlow()

    private val _modelo = MutableStateFlow("")
    val modelo: StateFlow<String> = _modelo.asStateFlow()

    private val _placa = MutableStateFlow("")
    val placa: StateFlow<String> = _placa.asStateFlow()

    private val _ano = MutableStateFlow("")
    val ano: StateFlow<String> = _ano.asStateFlow()

    private val _valorDiaria = MutableStateFlow("")
    val valorDiaria: StateFlow<String> = _valorDiaria.asStateFlow()

    private val _mensagemErro = MutableStateFlow<String?>(null)
    val mensagemErro: StateFlow<String?> = _mensagemErro.asStateFlow()

    private val _cadastroSucesso = MutableStateFlow(false)
    val cadastroSucesso: StateFlow<Boolean> = _cadastroSucesso.asStateFlow()

    fun onMarcaChanged(novaMarca: String) {
        _marca.value = novaMarca
    }

    fun onModeloChanged(novoModelo: String) {
        _modelo.value = novoModelo
    }

    fun onPlacaChanged(novaPlaca: String) {
        // Formata em maiúsculas automaticamente
        _placa.value = novaPlaca.uppercase().trim()
    }

    fun onAnoChanged(novoAno: String) {
        _ano.value = novoAno
    }

    fun onValorDiariaChanged(novoValor: String) {
        _valorDiaria.value = novoValor
    }

    fun limparMensagemErro() {
        _mensagemErro.value = null
    }

    fun resetarCadastroSucesso() {
        _cadastroSucesso.value = false
    }

    /**
     * Valida formato da placa brasileira (antigo AAA-1234 / AAA1234 ou Mercosul AAA1A23).
     */
    fun validarPlaca(placa: String): Boolean {
        val placaLimpa = placa.replace("-", "").uppercase().trim()
        val regexAntiga = Regex("^[A-Z]{3}[0-9]{4}$")
        val regexMercosul = Regex("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")
        return regexAntiga.matches(placaLimpa) || regexMercosul.matches(placaLimpa)
    }

    /**
     * Valida os campos obrigatórios e cadastra o novo veículo.
     */
    fun cadastrarVeiculo() {
        val mMarca = _marca.value.trim()
        val mModelo = _modelo.value.trim()
        val mPlaca = _placa.value.trim()
        val mAnoTexto = _ano.value.trim()
        val mValorTexto = _valorDiaria.value.trim().replace(",", ".")

        // RF02.3: Todos os campos são de preenchimento obrigatório
        if (mMarca.isBlank() || mModelo.isBlank() || mPlaca.isBlank() || mAnoTexto.isBlank() || mValorTexto.isBlank()) {
            _mensagemErro.value = "Preencha todos os campos obrigatórios."
            return
        }

        // RF02.3: Validação da placa brasileira
        if (!validarPlaca(mPlaca)) {
            _mensagemErro.value = "Placa inválida! Use o formato tradicional (ABC-1234) ou Mercosul (ABC1D23)."
            return
        }

        val anoInt = mAnoTexto.toIntOrNull()
        if (anoInt == null || anoInt < 1950 || anoInt > 2030) {
            _mensagemErro.value = "Informe um ano válido para o veículo."
            return
        }

        // RF02.3: Valor da diária deve ser um valor numérico positivo
        val valorDouble = mValorTexto.toDoubleOrNull()
        if (valorDouble == null || valorDouble <= 0.0) {
            _mensagemErro.value = "O valor da diária deve ser maior que zero."
            return
        }

        viewModelScope.launch {
            val novoVeiculo = VeiculoEntity(
                marca = mMarca,
                modelo = mModelo,
                placa = mPlaca,
                ano = anoInt,
                valorDiaria = valorDouble,
                status = StatusVeiculo.DISPONIVEL // RF02.4: Padrão DISPONÍVEL
            )
            repository.inserirVeiculo(novoVeiculo)

            // Limpa o formulário e notifica sucesso
            _marca.value = ""
            _modelo.value = ""
            _placa.value = ""
            _ano.value = ""
            _valorDiaria.value = ""
            _mensagemErro.value = null
            _cadastroSucesso.value = true
        }
    }

    /**
     * Alterna status do veículo entre DISPONIVEL e MANUTENCAO (caso não esteja ALUGADO).
     */
    fun alterarStatusManutencao(veiculo: VeiculoEntity) {
        if (veiculo.status == StatusVeiculo.ALUGADO) return

        viewModelScope.launch {
            val novoStatus = if (veiculo.status == StatusVeiculo.DISPONIVEL) {
                StatusVeiculo.MANUTENCAO
            } else {
                StatusVeiculo.DISPONIVEL
            }
            repository.atualizarStatus(veiculo.id, novoStatus)
        }
    }
}

/**
 * Factory para instanciar o VeiculoViewModel passando o repositório.
 */
class VeiculoViewModelFactory(private val repository: VeiculoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VeiculoViewModel::class.java)) {
            return VeiculoViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
