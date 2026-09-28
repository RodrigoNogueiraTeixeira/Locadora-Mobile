package com.example.locadora.data.contacts

import android.content.Context
import android.provider.ContactsContract
import com.example.locadora.data.model.ContatoDispositivo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gerenciador responsável por consultar contatos via ContentResolver (ContactsContract).
 */
class ContatosManager(private val context: Context) {

    /**
     * Consulta a agenda de contatos do dispositivo de forma assíncrona.
     */
    suspend fun buscarContatos(filtroNome: String = ""): List<ContatoDispositivo> = withContext(Dispatchers.IO) {
        val lista = mutableListOf<ContatoDispositivo>()
        val idsVistos = mutableSetOf<String>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val selection = if (filtroNome.isNotBlank()) {
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} LIKE ?"
        } else {
            null
        }

        val selectionArgs = if (filtroNome.isNotBlank()) {
            arrayOf("%$filtroNome%")
        } else {
            null
        }

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nomeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
                val telefoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (it.moveToNext()) {
                    val id = if (idIndex != -1) it.getString(idIndex) ?: "" else ""
                    val nome = if (nomeIndex != -1) it.getString(nomeIndex) ?: "Sem nome" else "Sem nome"
                    val telefone = if (telefoneIndex != -1) it.getString(telefoneIndex) ?: "" else ""

                    // Evita contatos duplicados com o mesmo telefone ou ID
                    val chave = "$id-$telefone"
                    if (!idsVistos.contains(chave) && nome.isNotBlank()) {
                        idsVistos.add(chave)
                        lista.add(
                            ContatoDispositivo(
                                id = id,
                                nome = nome,
                                telefone = telefone
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            // Caso a permissão não tenha sido concedida
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        lista
    }
}
