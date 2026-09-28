package com.example.locadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.locadora.ui.navigation.AppNavigation
import com.example.locadora.ui.theme.LocadoraTheme

/**
 * Activity principal do aplicativo (Arquitetura Single Activity com Navigation Compose).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocadoraTheme {
                AppNavigation()
            }
        }
    }
}