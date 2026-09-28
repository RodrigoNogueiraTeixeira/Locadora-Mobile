package com.example.locadora.ui.screens.veiculos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.locadora.data.local.entity.VeiculoEntity
import com.example.locadora.data.local.model.StatusVeiculo

/**
 * Tela de listagem da frota de veículos (RF02.1).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculosScreen(
    viewModel: VeiculoViewModel,
    onNavigateBack: () -> Unit
) {
    val veiculos by viewModel.veiculos.collectAsStateWithLifecycle()
    val cadastroSucesso by viewModel.cadastroSucesso.collectAsStateWithLifecycle()

    var showCadastroDialog by remember { mutableStateOf(false) }
    var filtroStatus by remember { mutableStateOf<StatusVeiculo?>(null) }

    LaunchedEffect(cadastroSucesso) {
        if (cadastroSucesso) {
            showCadastroDialog = false
            viewModel.resetarCadastroSucesso()
        }
    }

    val veiculosFiltrados = remember(veiculos, filtroStatus) {
        if (filtroStatus == null) {
            veiculos
        } else {
            veiculos.filter { it.status == filtroStatus }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Frota de Veículos") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.limparMensagemErro()
                    showCadastroDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Novo Veículo"
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateBack,
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Locações") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Já na frota */ },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                    label = { Text("Frota") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chips de filtro por status
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filtroStatus == null,
                        onClick = { filtroStatus = null },
                        label = { Text("Todos (${veiculos.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = filtroStatus == StatusVeiculo.DISPONIVEL,
                        onClick = {
                            filtroStatus = if (filtroStatus == StatusVeiculo.DISPONIVEL) null else StatusVeiculo.DISPONIVEL
                        },
                        label = { Text("Disponíveis") }
                    )
                }
                item {
                    FilterChip(
                        selected = filtroStatus == StatusVeiculo.ALUGADO,
                        onClick = {
                            filtroStatus = if (filtroStatus == StatusVeiculo.ALUGADO) null else StatusVeiculo.ALUGADO
                        },
                        label = { Text("Alugados") }
                    )
                }
                item {
                    FilterChip(
                        selected = filtroStatus == StatusVeiculo.MANUTENCAO,
                        onClick = {
                            filtroStatus = if (filtroStatus == StatusVeiculo.MANUTENCAO) null else StatusVeiculo.MANUTENCAO
                        },
                        label = { Text("Manutenção") }
                    )
                }
            }

            if (veiculosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = if (veiculos.isEmpty())
                                "Nenhum veículo cadastrado na frota.\nToque no botão '+' para adicionar."
                            else
                                "Nenhum veículo com o status selecionado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(veiculosFiltrados, key = { it.id }) { veiculo ->
                        VeiculoCard(
                            veiculo = veiculo,
                            onToggleManutencao = { viewModel.alterarStatusManutencao(veiculo) }
                        )
                    }
                }
            }
        }
    }

    if (showCadastroDialog) {
        CadastroVeiculoDialog(
            viewModel = viewModel,
            onDismiss = { showCadastroDialog = false }
        )
    }
}

/**
 * Card para exibição individual de um veículo.
 */
@Composable
fun VeiculoCard(
    veiculo: VeiculoEntity,
    onToggleManutencao: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${veiculo.marca} ${veiculo.modelo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = veiculo.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Placa: ${veiculo.placa}  •  Ano: ${veiculo.ano}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "R$ ${"%.2f".format(veiculo.valorDiaria)}/dia",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Ação de manutenção só permitida se não estiver alugado
            if (veiculo.status != StatusVeiculo.ALUGADO) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onToggleManutencao,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = if (veiculo.status == StatusVeiculo.DISPONIVEL)
                            Icons.Default.Build
                        else
                            Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = if (veiculo.status == StatusVeiculo.DISPONIVEL)
                            "Colocar em Manutenção"
                        else
                            "Liberar para Disponível"
                    )
                }
            }
        }
    }
}

/**
 * Indicador visual (Badge) do status do veículo.
 */
@Composable
fun StatusBadge(status: StatusVeiculo) {
    val (backgroundColor, textColor, label) = when (status) {
        StatusVeiculo.DISPONIVEL -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "DISPONÍVEL")
        StatusVeiculo.ALUGADO -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "ALUGADO")
        StatusVeiculo.MANUTENCAO -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "MANUTENÇÃO")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
