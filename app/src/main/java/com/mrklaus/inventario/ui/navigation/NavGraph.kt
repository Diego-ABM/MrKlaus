package com.mrklaus.inventario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mrklaus.inventario.ui.screens.agregar.AgregarProductoScreen
import com.mrklaus.inventario.ui.screens.detalle.DetalleProductoScreen
import com.mrklaus.inventario.ui.screens.lista.ListaProductosScreen
import com.mrklaus.inventario.ui.screens.lista.FavoritosScreen
import com.mrklaus.inventario.ui.screens.alertas.AlertasScreen
import com.mrklaus.inventario.ui.screens.pedidos.PedidosScreen
import com.mrklaus.inventario.ui.screens.ventas.VentasScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Lista.route,
        modifier = modifier
    ) {
        composable(Screen.Lista.route) {
            ListaProductosScreen(
                onNavigateToDetalle = { id ->
                    navController.navigate(Screen.Detalle.createRoute(id))
                },
                onNavigateToAgregar = {
                    navController.navigate(Screen.Agregar.route)
                }
            )
        }
        
        composable(
            route = Screen.Detalle.route,
            arguments = listOf(navArgument("productoId") { type = NavType.LongType })
        ) {
            DetalleProductoScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        composable(Screen.Agregar.route) {
            AgregarProductoScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Favoritos.route) {
            FavoritosScreen(
                onNavigateToDetalle = { id ->
                    navController.navigate(Screen.Detalle.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Alertas.route) {
            AlertasScreen(
                onNavigateToDetalle = { id ->
                    navController.navigate(Screen.Detalle.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Pedidos.route) {
            PedidosScreen(
                onNavigateToDetalle = { id ->
                    navController.navigate(Screen.Detalle.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Ventas.route) {
            VentasScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
