package com.mrklaus.inventario.ui.screens.pedidos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.ui.components.ProductoCard
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PedidosViewModel = hiltViewModel()
) {
    val productos by viewModel.paraPedido.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos al Proveedor") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (productos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No hay productos marcados para pedido")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                items(productos) { producto ->
                    Column {
                        ProductoCard(
                            producto = producto,
                            onClick = { onNavigateToDetalle(producto.id) }
                        )
                        producto.notaPedido?.let {
                            Text(
                                text = "Nota: $it",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(start = Spacing.medium, top = Spacing.extraSmall)
                            )
                        }
                    }
                }
            }
        }
    }
}
