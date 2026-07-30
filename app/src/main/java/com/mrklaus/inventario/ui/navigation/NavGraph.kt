package com.mrklaus.inventario.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mrklaus.inventario.ui.screens.agregar.AgregarProductoScreen
import com.mrklaus.inventario.ui.screens.detalle.DetalleProductoScreen
import com.mrklaus.inventario.ui.screens.lista.ListaProductosScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Lista.route
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
    }
}
