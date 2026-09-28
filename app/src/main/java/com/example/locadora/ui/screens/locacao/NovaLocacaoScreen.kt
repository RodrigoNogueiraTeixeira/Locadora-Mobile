package com.example.locadora.ui.screens.locacao

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.ui.screens.contatos.ContatosViewModel
import com.example.locadora.ui.screens.contatos.SelecaoContatoModal
import com.example.locadora.util.DateUtils

/**
 * Tela de abertura de nova locação (RF04).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaLocacaoScreen(
    viewModel: NovaLocacaoViewModel,
    contatosViewModel: ContatosViewModel,
    onNavigateToVeiculos: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val veiculosDisponiveis by viewModel.veiculosDisponiveis.collectAsStateWithLifecycle()
    val veiculoSelecionado by viewModel.veiculoSelecionado.collectAsStateWithLifecycle()
    val clienteSelecionado by viewModel.clienteSelecionado.collectAsStateWithLifecycle()
    val dataSaidaMillis by viewModel.dataSaidaMillis.collectAsStateWithLifecycle()
    val dataEntregaMillis by viewModel.dataEntregaMillis.collectAsStateWithLifecycle()
    val quantidadeDias by viewModel.quantidadeDias.collectAsStateWithLifecycle()
    val valorTotalEstimado by viewModel.valorTotalEstimado.collectAsStateWithLifecycle()
    val mensagemErro by viewModel.mensagemErro.collectAsStateWithLifecycle()
    val locacaoSalvaSucesso by viewModel.locacaoSalvaSucesso.collectAsStateWithLifecycle()

    var showContatosModal by remember { mutableStateOf(false) }
    var showDatePickerSaida by remember { mutableStateOf(false) }
    var showDatePickerEntrega by remember { mutableStateOf(false) }
    var dropdownVeiculosExpandido by remember { mutableStateOf(false) }

    // Auto-seleciona o primeiro veículo disponível se nenhum estiver selecionado
    LaunchedEffect(veiculosDisponiveis) {
        if (veiculoSelecionado == null && veiculosDisponiveis.isNotEmpty()) {
            viewModel.selecionarVeiculo(veiculosDisponiveis.first())
        }
    }

    // RF04.5: Retorna ao Dashboard ao confirmar com sucesso
    LaunchedEffect(locacaoSalvaSucesso) {
        if (locacaoSalvaSucesso) {
            viewModel.resetarSucesso()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Abertura de Locação") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seção 1: Seleção de Veículo (RF04.1)
            Text(
                text = "1. Veículo Disponível",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (veiculosDisponiveis.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Não há veículos disponíveis para locação no momento. Cadastre ou libere um veículo na frota.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToVeiculos,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cadastrar Veículo na Frota")
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { dropdownVeiculosExpandido = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    if (veiculoSelecionado != null) {
                                        Text(
                                            text = "${veiculoSelecionado!!.marca} ${veiculoSelecionado!!.modelo}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Placa: ${veiculoSelecionado!!.placa}  •  R$ ${"%.2f".format(veiculoSelecionado!!.valorDiaria)}/dia",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            text = "Toque para escolher o veículo...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownVeiculosExpandido,
                        onDismissRequest = { dropdownVeiculosExpandido = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        veiculosDisponiveis.forEach { veiculo ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "${veiculo.marca} ${veiculo.modelo} (${veiculo.placa})",
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Diária: R$ ${"%.2f".format(veiculo.valorDiaria)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.selecionarVeiculo(veiculo)
                                    dropdownVeiculosExpandido = false
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Seção 2: Seleção de Cliente via Contatos (RF04.2)
            Text(
                text = "2. Cliente (Agenda do Dispositivo)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (clienteSelecionado == null) {
                OutlinedButton(
                    onClick = { showContatosModal = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar Cliente nos Contatos")
                }
            } else {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = clienteSelecionado!!.nome,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = clienteSelecionado!!.telefone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        TextButton(onClick = { showContatosModal = true }) {
                            Text("Trocar")
                        }
                    }
                }
            }

            HorizontalDivider()

            // Seção 3: Período da Locação (RF04.3)
            Text(
                text = "3. Período da Locação",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Seletor Data de Saída
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePickerSaida = true }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Data de Saída",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DateUtils.formatarData(dataSaidaMillis),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Seletor Data Prevista de Entrega
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePickerEntrega = true }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Entrega Prevista",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DateUtils.formatarData(dataEntregaMillis),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            HorizontalDivider()

            // Seção 4: Cálculo Automático do Valor Total (RF04.4)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumo Estimado da Locação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Duração prevista:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$quantidadeDias ${if (quantidadeDias == 1L) "diária" else "diárias"}",
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (veiculoSelecionado != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Valor da diária:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "R$ ${"%.2f".format(veiculoSelecionado!!.valorDiaria)}",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL ESTIMADO:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "R$ ${"%.2f".format(valorTotalEstimado)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Exibição de mensagem de erro
            if (mensagemErro != null) {
                Text(
                    text = mensagemErro ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Confirmar Locação (RF04.5)
            Button(
                onClick = { viewModel.confirmarLocacao() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = veiculoSelecionado != null && clienteSelecionado != null
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirmar Locação",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Modal de seleção de contatos
    if (showContatosModal) {
        SelecaoContatoModal(
            viewModel = contatosViewModel,
            onContatoSelecionado = { contato ->
                viewModel.selecionarCliente(contato)
            },
            onDismiss = { showContatosModal = false }
        )
    }

    // DatePicker para Data de Saída
    if (showDatePickerSaida) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataSaidaMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePickerSaida = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.setDataSaida(it) }
                        showDatePickerSaida = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerSaida = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // DatePicker para Data de Entrega Prevista
    if (showDatePickerEntrega) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dataEntregaMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePickerEntrega = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.setDataEntrega(it) }
                        showDatePickerEntrega = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerEntrega = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
