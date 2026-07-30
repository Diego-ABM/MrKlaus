package com.mrklaus.inventario.ui.screens.detalle

import com.mrklaus.inventario.domain.model.Producto

sealed class DetalleUiState {
    object Cargando : DetalleUiState()
    data class Exito(val producto: Producto) : DetalleUiState()
    object NoEncontrado : DetalleUiState()
    data class Error(val mensaje: String) : DetalleUiState()
}
