package com.mrklaus.inventario.ui.screens.lista

import com.mrklaus.inventario.domain.model.Producto

sealed class ListaUiState {
    object Cargando : ListaUiState()
    data class Exito(val productos: List<Producto>) : ListaUiState()
    object Vacio : ListaUiState()
    data class Error(val mensaje: String) : ListaUiState()
}
