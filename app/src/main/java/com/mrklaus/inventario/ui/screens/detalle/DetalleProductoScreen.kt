package com.mrklaus.inventario.ui.screens.detalle

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    onNavigateBack: () -> Unit,
    viewModel: DetalleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.agregarFoto(it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is DetalleUiState.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DetalleUiState.Exito -> {
                val producto = state.producto
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Carrusel de imágenes
                    val pagerState = rememberPagerState(pageCount = { (producto.fotos.size).coerceAtLeast(1) })
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        if (producto.fotos.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                AsyncImage(
                                    model = producto.fotos[page],
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            // Botón Borrar Foto actual
                            FilledIconButton(
                                onClick = { viewModel.eliminarFoto(producto.fotos[pagerState.currentPage]) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(Spacing.small),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "Borrar Foto", tint = Color.White)
                            }
                        } else {
                            // Placeholder
                            Box(
                                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sin imágenes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Botón Añadir Foto
                        if (producto.fotos.size < 5) {
                            FilledIconButton(
                                onClick = { 
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(Spacing.small),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Añadir Foto", tint = Color.White)
                            }
                        }
                        
                        // Indicadores
                        if (producto.fotos.size > 1) {
                            Row(
                                Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(producto.fotos.size) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(Spacing.medium)) {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "${producto.precio} COP",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = Spacing.small)
                        )

                        // Badge de Mascota y Categoría
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                            SuggestionChip(onClick = {}, label = { Text(producto.mascota.name) })
                            SuggestionChip(onClick = {}, label = { Text(producto.categoria.name) })
                        }

                        if (producto.nota != null) {
                            Card(
                                modifier = Modifier.padding(vertical = Spacing.medium),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Row(modifier = Modifier.padding(Spacing.medium), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.width(Spacing.small))
                                    Text(text = producto.nota, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }

                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = Spacing.medium)
                        )
                        Text(
                            text = producto.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = Spacing.small)
                        )

                        Divider(modifier = Modifier.padding(vertical = Spacing.large))

                        // Gestión de Stock
                        Text(text = "Inventario", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Cantidad actual: ${producto.cantidadStock}")
                            Row {
                                Button(onClick = { viewModel.actualizarStock(producto.cantidadStock - 1) }) {
                                    Text("-")
                                }
                                Spacer(Modifier.width(Spacing.small))
                                Button(onClick = { viewModel.actualizarStock(producto.cantidadStock + 1) }) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarProducto {
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
