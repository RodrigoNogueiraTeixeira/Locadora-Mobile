package com.example.locadora.ui.screens.veiculos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Diálogo de cadastro de novos veículos com validações visuais (RF02.2 e RF02.3).
 */
@Composable
fun CadastroVeiculoDialog(
    viewModel: VeiculoViewModel,
    onDismiss: () -> Unit
) {
    val marca by viewModel.marca.collectAsStateWithLifecycle()
    val modelo by viewModel.modelo.collectAsStateWithLifecycle()
    val placa by viewModel.placa.collectAsStateWithLifecycle()
    val ano by viewModel.ano.collectAsStateWithLifecycle()
    val valorDiaria by viewModel.valorDiaria.collectAsStateWithLifecycle()
    val mensagemErro by viewModel.mensagemErro.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cadastrar Novo Veículo",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = marca,
                    onValueChange = viewModel::onMarcaChanged,
                    label = { Text("Marca (ex: Fiat, Toyota)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = modelo,
                    onValueChange = viewModel::onModeloChanged,
                    label = { Text("Modelo (ex: Uno, Corolla)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = placa,
                    onValueChange = viewModel::onPlacaChanged,
                    label = { Text("Placa (ex: ABC1D23 ou ABC-1234)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ano,
                        onValueChange = viewModel::onAnoChanged,
                        label = { Text("Ano") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = valorDiaria,
                        onValueChange = viewModel::onValorDiariaChanged,
                        label = { Text("Diária (R$)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (mensagemErro != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mensagemErro ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.cadastrarVeiculo()
                }
            ) {
                Text("Cadastrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
