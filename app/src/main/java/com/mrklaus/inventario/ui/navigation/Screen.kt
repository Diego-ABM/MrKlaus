package com.mrklaus.inventario.ui.navigation

sealed class Screen(val route: String) {
    object Lista : Screen("lista")
    object Detalle : Screen("detalle/{productoId}") {
        fun createRoute(productoId: Long) = "detalle/$productoId"
    }
    object Agregar : Screen("agregar")
    object Favoritos : Screen("favoritos")
    object Alertas : Screen("alertas")
    object Pedidos : Screen("pedidos")
    object Ventas : Screen("ventas")
    object Editar : Screen("editar/{productoId}") {
        fun createRoute(productoId: Long) = "editar/$productoId"
    }
    object Backup : Screen("backup")
}
