package com.example.locadora.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utilitários para formatação e cálculos de datas no aplicativo.
 */
object DateUtils {

    private val formatoBrasileiro = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    /**
     * Formata um timestamp em milissegundos para o formato dd/MM/yyyy.
     */
    fun formatarData(timestampMillis: Long): String {
        return formatoBrasileiro.format(Date(timestampMillis))
    }

    /**
     * Calcula dinamicamente os dias faltantes para a entrega com base na data atual.
     * Retorna um número negativo se estiver em atraso.
     */
    fun calcularDiasFaltantes(dataEntregaMillis: Long): Long {
        val hoje = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val entrega = Calendar.getInstance().apply {
            timeInMillis = dataEntregaMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val diferencaMillis = entrega - hoje
        return TimeUnit.MILLISECONDS.toDays(diferencaMillis)
    }

    /**
     * Calcula a quantidade de dias entre a data de saída e a de entrega prevista.
     * Garante no mínimo 1 diária caso a entrega seja no mesmo dia.
     */
    fun calcularQuantidadeDias(dataSaidaMillis: Long, dataEntregaMillis: Long): Long {
        val diferenca = dataEntregaMillis - dataSaidaMillis
        val dias = TimeUnit.MILLISECONDS.toDays(diferenca)
        return if (dias <= 0) 1 else dias
    }
}
