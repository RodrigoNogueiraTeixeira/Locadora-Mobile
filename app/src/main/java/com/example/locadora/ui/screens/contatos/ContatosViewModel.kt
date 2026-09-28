package com.example.locadora.ui.screens.contatos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.locadora.data.contacts.ContatosManager
import com.example.locadora.data.model.ContatoDispositivo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela integração com contatos do dispositivo (RF03).
 */
class ContatosViewModel(private val contatosManager: ContatosManager) : ViewModel() {

    private val _contatos = MutableStateFlow<List<ContatoDispositivo>>(emptyList())
    val contatos: StateFlow<List<ContatoDispositivo>> = _contatos.asStateFlow()

    private val _filtro = MutableStateFlow("")
    val filtro: StateFlow<String> = _filtro.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _permissaoNegada = MutableStateFlow(false)
    val permissaoNegada: StateFlow<Boolean> = _permissaoNegada.asStateFlow()

    private var buscaJob: Job? = null

    fun onPermissaoConcedida() {
        _permissaoNegada.value = false
        carregarContatos(_filtro.value)
    }

    fun onPermissaoNegada() {
        _permissaoNegada.value = true
        _carregando.value = false
    }

    fun onFiltroChanged(novoFiltro: String) {
        _filtro.value = novoFiltro
        // Debounce simples para busca fluida em tempo real
        buscaJob?.cancel()
        buscaJob = viewModelScope.launch {
            delay(150)
            carregarContatos(novoFiltro)
        }
    }

    fun recarregar() {
        carregarContatos(_filtro.value)
    }

    private fun carregarContatos(query: String) {
        viewModelScope.launch {
            _carregando.value = true
            try {
                val resultado = contatosManager.buscarContatos(query)
                _contatos.value = resultado
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _carregando.value = false
            }
        }
    }
}

/**
 * Factory para instanciar o ContatosViewModel.
 */
class ContatosViewModelFactory(private val contatosManager: ContatosManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContatosViewModel::class.java)) {
            return ContatosViewModel(contatosManager) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
