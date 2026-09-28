package com.example.locadora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locadora.LocadoraApplication
import com.example.locadora.data.contacts.ContatosManager
import com.example.locadora.ui.screens.contatos.ContatosViewModel
import com.example.locadora.ui.screens.contatos.ContatosViewModelFactory
import com.example.locadora.ui.screens.dashboard.DashboardScreen
import com.example.locadora.ui.screens.dashboard.DashboardViewModel
import com.example.locadora.ui.screens.dashboard.DashboardViewModelFactory
import com.example.locadora.ui.screens.locacao.NovaLocacaoScreen
import com.example.locadora.ui.screens.locacao.NovaLocacaoViewModel
import com.example.locadora.ui.screens.locacao.NovaLocacaoViewModelFactory
import com.example.locadora.ui.screens.sync.SincronizacaoScreen
import com.example.locadora.ui.screens.veiculos.VeiculoViewModel
import com.example.locadora.ui.screens.veiculos.VeiculoViewModelFactory
import com.example.locadora.ui.screens.veiculos.VeiculosScreen

/**
 * Definição das rotas de navegação do aplicativo.
 */
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Veiculos : Screen("veiculos")
    object NovaLocacao : Screen("nova_locacao")
    object Sincronizacao : Screen("sincronizacao")
}

/**
 * Grafo de navegação central do aplicativo em padrão Single Activity (Navigation Compose).
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as LocadoraApplication

    // Factories dos ViewModels
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(app.locacaoRepository)
    )

    val veiculoViewModel: VeiculoViewModel = viewModel(
        factory = VeiculoViewModelFactory(app.veiculoRepository)
    )

    val contatosViewModel: ContatosViewModel = viewModel(
        factory = ContatosViewModelFactory(ContatosManager(context))
    )

    val novaLocacaoViewModel: NovaLocacaoViewModel = viewModel(
        factory = NovaLocacaoViewModelFactory(
            veiculoRepository = app.veiculoRepository,
            clienteRepository = app.clienteRepository,
            locacaoRepository = app.locacaoRepository
        )
    )

    // RF01.1: O app inicia diretamente na tela de Dashboard
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToNovaLocacao = {
                    navController.navigate(Screen.NovaLocacao.route)
                },
                onNavigateToVeiculos = {
                    navController.navigate(Screen.Veiculos.route)
                },
                onNavigateToSincronizacao = {
                    navController.navigate(Screen.Sincronizacao.route)
                }
            )
        }

        composable(Screen.Veiculos.route) {
            VeiculosScreen(
                viewModel = veiculoViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.NovaLocacao.route) {
            NovaLocacaoScreen(
                viewModel = novaLocacaoViewModel,
                contatosViewModel = contatosViewModel,
                onNavigateToVeiculos = {
                    navController.navigate(Screen.Veiculos.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Sincronizacao.route) {
            SincronizacaoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
