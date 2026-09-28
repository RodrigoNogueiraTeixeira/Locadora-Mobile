package com.example.locadora.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.locadora.data.local.dao.ClienteDao
import com.example.locadora.data.local.dao.LocacaoDao
import com.example.locadora.data.local.dao.VeiculoDao
import com.example.locadora.data.local.entity.ClienteEntity
import com.example.locadora.data.local.entity.LocacaoEntity
import com.example.locadora.data.local.entity.VeiculoEntity

/**
 * Banco de dados Room principal do aplicativo.
 * Configurado com as três entidades: Veículo, Cliente (cache) e Locação.
 */
@Database(
    entities = [
        VeiculoEntity::class,
        ClienteEntity::class,
        LocacaoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun veiculoDao(): VeiculoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun locacaoDao(): LocacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "locadora_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
