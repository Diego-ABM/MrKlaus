package com.mrklaus.inventario.ui.screens.agregar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.ui.components.FormularioProducto
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    onNavigateBack: () -> Unit,
    viewModel: AgregarViewModel = hiltViewModel(),
) {
    val nombre by viewModel.nombre.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val precio by viewModel.precio.collectAsState()
    val precioCompra by viewModel.precioCompra.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val pesoKg by viewModel.pesoKg.collectAsState()
    val fechaVencimiento by viewModel.fechaVencimiento.collectAsState()
    val mascota by viewModel.mascota.collectAsState()
    val categoria by viewModel.categoria.collectAsState()
    val fotos by viewModel.fotosSeleccionadas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(Spacing.medium)) {
            FormularioProducto(
                nombre = nombre,
                onNombreChange = viewModel::onNombreChange,
                descripcion = descripcion,
                onDescripcionChange = viewModel::onDescripcionChange,
                precioCompra = precioCompra,
                onPrecioCompraChange = viewModel::onPrecioCompraChange,
                precioVenta = precio,
                onPrecioVentaChange = viewModel::onPrecioChange,
                stock = stock,
                onStockChange = viewModel::onStockChange,
                pesoKg = pesoKg,
                onPesoKgChange = viewModel::onPesoKgChange,
                mascota = mascota,
                onMascotaChange = viewModel::onMascotaChange,
                categoria = categoria,
                onCategoriaChange = viewModel::onCategoriaChange,
                fotos = fotos,
                onAddFoto = viewModel::addFoto,
                onRemoveFoto = { index -> viewModel.removeFoto(fotos[index]) },
                fechaVencimiento = fechaVencimiento,
                onFechaVencimientoChange = viewModel::onFechaVencimientoChange,
                botonTexto = "Guardar Producto",
                onBotonClick = { viewModel.guardarProducto(onNavigateBack) },
                botonHabilitado = nombre.isNotBlank() && precio.isNotBlank()
            )
        }
    }
}
