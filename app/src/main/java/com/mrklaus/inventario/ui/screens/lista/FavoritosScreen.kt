package com.mrklaus.inventario.ui.screens.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.ui.components.ProductoCard
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FavoritosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ListaUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ListaUiState.Exito -> {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        items(state.productos) { producto ->
                            ProductoCard(
                                producto = producto,
                                onClick = { onNavigateToDetalle(producto.id) },
                                onToggleFavorite = { viewModel.toggleFavorito(producto.id) }
                            )
                        }
                    }
                }
                is ListaUiState.Vacio -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes productos favoritos aún")
                    }
                }
                is ListaUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.mensaje}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
