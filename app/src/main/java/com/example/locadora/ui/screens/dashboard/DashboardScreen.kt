package com.example.locadora.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.locadora.data.local.model.LocacaoCompleta
import com.example.locadora.util.DateUtils

/**
 * Tela inicial de Dashboard de Locações Ativas (RF01).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToNovaLocacao: () -> Unit,
    onNavigateToVeiculos: () -> Unit,
    onNavigateToSincronizacao: () -> Unit
) {
    val locacoesAtivas by viewModel.locacoesAtivas.collectAsStateWithLifecycle()
    val mensagemFeedback by viewModel.mensagemFeedback.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var locacaoParaFinalizar by remember { mutableStateOf<LocacaoCompleta?>(null) }

    LaunchedEffect(mensagemFeedback) {
        mensagemFeedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparFeedback()
        }
    }

    val totalAtivas = locacoesAtivas.size
    val totalAtrasadas = locacoesAtivas.count {
        DateUtils.calcularDiasFaltantes(it.locacao.dataPrevistaEntrega) < 0
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Locadora de Veículos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Painel de Locações",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToVeiculos,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Frota de Veículos"
                        )
                    }
                    IconButton(onClick = onNavigateToSincronizacao) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sincronização API"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // RF01.5: Botão de Ação Flutuante (FAB) para iniciar cadastro de "Nova Locação"
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNovaLocacao,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova Locação") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cards informativos de status rápido
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Locações Ativas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalAtivas",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalAtrasadas > 0)
                            Color(0xFFFFEBEE)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Em Atraso",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (totalAtrasadas > 0) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalAtrasadas",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (totalAtrasadas > 0) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // RF01.2: Lista apenas as locações com status ATIVA
            if (locacoesAtivas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhuma locação ativa no momento.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toque em '+ Nova Locação' para abrir um novo contrato de aluguel de veículo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(locacoesAtivas, key = { it.locacao.id }) { item ->
                        LocacaoCard(
                            locacaoCompleta = item,
                            onFinalizarClick = { locacaoParaFinalizar = item }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmação para finalizar devolução
    locacaoParaFinalizar?.let { item ->
        AlertDialog(
            onDismissRequest = { locacaoParaFinalizar = null },
            title = { Text("Devolução do Veículo") },
            text = {
                Text(
                    "Deseja confirmar a entrega e devolução do veículo ${item.veiculo.marca} ${item.veiculo.modelo} (${item.veiculo.placa})? O veículo retornará para o status DISPONÍVEL."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.finalizarLocacao(item)
                        locacaoParaFinalizar = null
                    }
                ) {
                    Text("Confirmar Devolução")
                }
            },
            dismissButton = {
                TextButton(onClick = { locacaoParaFinalizar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * RF01.3 e RF01.4: Card de exibição da locação ativa com dados completos e cálculo dinâmico de dias.
 */
@Composable
fun LocacaoCard(
    locacaoCompleta: LocacaoCompleta,
    onFinalizarClick: () -> Unit
) {
    val veiculo = locacaoCompleta.veiculo
    val cliente = locacaoCompleta.cliente
    val locacao = locacaoCompleta.locacao

    // RF01.3: Quantidade de dias faltantes calculada dinamicamente
    val diasFaltantes = DateUtils.calcularDiasFaltantes(locacao.dataPrevistaEntrega)
    val estaEmAtraso = diasFaltantes < 0

    // RF01.4: Destacar visualmente locações em atraso (borda vermelha e fundo alertando)
    val cardBorder = if (estaEmAtraso) {
        BorderStroke(1.5.dp, Color(0xFFD32F2F))
    } else {
        null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estaEmAtraso) Color(0xFFFFFBFA) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha do Veículo e Badge de Prazo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // RF01.3: Modelo, Marca e Placa
                    Text(
                        text = "${veiculo.marca} ${veiculo.modelo}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // RF01.4: Indicador de dias faltantes com destaque em atraso
                DiasFaltantesBadge(diasFaltantes = diasFaltantes)
            }

            Text(
                text = "Placa: ${veiculo.placa}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 30.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // RF01.3: Dados do Cliente (Nome e Telefone)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cliente.nome,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (cliente.telefone.isNotBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cliente.telefone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // RF01.3: Datas formatadas em dd/MM/yyyy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saída: ${DateUtils.formatarData(locacao.dataSaida)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (estaEmAtraso) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Entrega: ${DateUtils.formatarData(locacao.dataPrevistaEntrega)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (estaEmAtraso) FontWeight.Bold else FontWeight.Normal,
                        color = if (estaEmAtraso) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linha com Valor Total e Botão para Finalizar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: R$ ${"%.2f".format(locacao.valorTotal)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = onFinalizarClick
                ) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Finalizar Locação")
                }
            }
        }
    }
}

/**
 * RF01.4: Badge visual com cores e texto baseados nos dias faltantes para devolução.
 */
@Composable
fun DiasFaltantesBadge(diasFaltantes: Long) {
    val (fundo, texto, label) = when {
        diasFaltantes < 0 -> {
            val dias = kotlin.math.abs(diasFaltantes)
            Triple(
                Color(0xFFFFEBEE),
                Color(0xFFC62828),
                "ATRASADO ($dias ${if (dias == 1L) "dia" else "dias"})"
            )
        }
        diasFaltantes == 0L -> {
            Triple(
                Color(0xFFFFF8E1),
                Color(0xFFF57F17),
                "ENTREGA HOJE"
            )
        }
        else -> {
            Triple(
                Color(0xFFE8F5E9),
                Color(0xFF2E7D32),
                "Faltam $diasFaltantes ${if (diasFaltantes == 1L) "dia" else "dias"}"
            )
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fundo)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (diasFaltantes < 0) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = texto,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = label,
                color = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
