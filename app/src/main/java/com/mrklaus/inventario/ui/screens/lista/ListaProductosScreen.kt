package com.mrklaus.inventario.ui.screens.lista

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.ui.components.ProductoCard
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProductosScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateToAgregar: () -> Unit,
    onNavigateToBackup: () -> Unit,
    viewModel: ListaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.queryBusqueda.collectAsState()
    val mascotaSeleccionada by viewModel.mascotaFiltro.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaFiltro.collectAsState()

    var titleVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { titleVisible = true }

    Scaffold(
        topBar = {
            val gradient = Brush.horizontalGradient(
                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
            ) {
                TopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = titleVisible,
                            enter = fadeIn(animationSpec = tween(1000)) + slideInHorizontally(animationSpec = tween(1000))
                        ) {
                            Text(
                                "Mr. Klaus Inventario",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToBackup) {
                            Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAgregar) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Buscador
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.buscar(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.medium),
                placeholder = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Filtros de Mascota
            ScrollableTabRow(
                selectedTabIndex = if (mascotaSeleccionada == null) 0 else mascotaSeleccionada!!.ordinal + 1,
                edgePadding = Spacing.medium,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {}
            ) {
                Tab(
                    selected = mascotaSeleccionada == null,
                    onClick = { viewModel.filtrarPorMascota(null) },
                    text = { Text("Todos") }
                )
                Mascota.entries.forEach { mascota ->
                    Tab(
                        selected = mascotaSeleccionada == mascota,
                        onClick = { viewModel.filtrarPorMascota(mascota) },
                        text = { Text(mascota.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Filtros de Categoría
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.medium, vertical = Spacing.small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                contentPadding = PaddingValues(end = Spacing.medium)
            ) {
                item {
                    FilterChip(
                        selected = categoriaSeleccionada == null,
                        onClick = { viewModel.filtrarPorCategoria(null) },
                        label = { Text("Todas") }
                    )
                }
                items(Categoria.entries) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { viewModel.filtrarPorCategoria(categoria) },
                        label = { Text(categoria.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Lista de productos
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
                        Text("No se encontraron productos")
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
