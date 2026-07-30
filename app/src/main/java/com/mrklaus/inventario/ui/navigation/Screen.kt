package com.mrklaus.inventario.ui.navigation

sealed class Screen(val route: String) {
    object Lista : Screen("lista")
    object Detalle : Screen("detalle/{productoId}") {
        fun createRoute(productoId: Long) = "detalle/$productoId"
    }
    object Agregar : Screen("agregar")
}
