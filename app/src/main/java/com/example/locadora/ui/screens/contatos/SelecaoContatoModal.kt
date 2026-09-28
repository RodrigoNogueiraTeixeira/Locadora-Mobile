package com.example.locadora.ui.screens.contatos

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.locadora.data.model.ContatoDispositivo

/**
 * Modal de busca e seleção de clientes da agenda do celular (RF03).
 */
@Composable
fun SelecaoContatoModal(
    viewModel: ContatosViewModel,
    onContatoSelecionado: (ContatoDispositivo) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val contatos by viewModel.contatos.collectAsStateWithLifecycle()
    val filtro by viewModel.filtro.collectAsStateWithLifecycle()
    val carregando by viewModel.carregando.collectAsStateWithLifecycle()
    val permissaoNegada by viewModel.permissaoNegada.collectAsStateWithLifecycle()

    // Launcher para solicitar permissão em tempo de execução (RF03.1)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) {
            viewModel.onPermissaoConcedida()
        } else {
            viewModel.onPermissaoNegada()
        }
    }

    // Verifica permissão ao abrir o diálogo
    LaunchedEffect(Unit) {
        val statusPermissao = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        )
        if (statusPermissao == PackageManager.PERMISSION_GRANTED) {
            viewModel.onPermissaoConcedida()
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selecionar Cliente",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp, max = 450.dp)
            ) {
                if (permissaoNegada) {
                    // RF03.2: Tratamento amigável de permissão negada
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Permissão necessária!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Para vincular o cliente à locação, o aplicativo precisa de permissão para ler a lista de contatos do seu aparelho.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        ) {
                            Text("Tentar Novamente")
                        }
                    }
                } else {
                    // RF03.4: Campo de busca por nome em tempo real
                    OutlinedTextField(
                        value = filtro,
                        onValueChange = viewModel::onFiltroChanged,
                        placeholder = { Text("Buscar contato por nome...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                        },
                        trailingIcon = {
                            if (filtro.isNotBlank()) {
                                IconButton(onClick = { viewModel.onFiltroChanged("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    if (carregando) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (contatos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (filtro.isBlank())
                                    "Nenhum contato encontrado na agenda."
                                else
                                    "Nenhum contato com o nome \"$filtro\".",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        // RF03.3 & RF03.5: Lista de contatos da agenda
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(contatos, key = { "${it.id}-${it.telefone}" }) { contato ->
                                ItemContatoRow(
                                    contato = contato,
                                    onClick = {
                                        onContatoSelecionado(contato)
                                        onDismiss()
                                    }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Linha individual com avatar, nome e telefone do contato.
 */
@Composable
private fun ItemContatoRow(
    contato: ContatoDispositivo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val inicial = contato.nome.trim().firstOrNull()?.uppercaseChar()
            if (inicial != null && inicial.isLetter()) {
                Text(
                    text = inicial.toString(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contato.nome,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (contato.telefone.isNotBlank()) {
                Text(
                    text = contato.telefone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
