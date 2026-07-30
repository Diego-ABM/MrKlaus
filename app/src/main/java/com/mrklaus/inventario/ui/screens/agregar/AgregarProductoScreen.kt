package com.mrklaus.inventario.ui.screens.agregar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgregarProductoScreen(
    onNavigateBack: () -> Unit,
    viewModel: AgregarViewModel = hiltViewModel()
) {
    val nombre by viewModel.nombre.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val precio by viewModel.precio.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val mascotaSeleccionada by viewModel.mascota.collectAsState()
    val categoriaSeleccionada by viewModel.categoria.collectAsState()
    val fotosSeleccionadas by viewModel.fotosSeleccionadas.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.addFoto(it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            // Carrusel de selección de fotos
            val pagerState = rememberPagerState(pageCount = { (fotosSeleccionadas.size).coerceAtLeast(1) })
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (fotosSeleccionadas.isNotEmpty()) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = fotosSeleccionadas[page],
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            FilledIconButton(
                                onClick = { viewModel.removeFoto(fotosSeleccionadas[page]) },
                                modifier = Modifier.align(Alignment.TopEnd).padding(Spacing.small),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "Remover", tint = Color.White)
                            }
                        }
                    }
                    
                    // Indicadores
                    if (fotosSeleccionadas.size > 1) {
                        Row(
                            Modifier.height(40.dp).fillMaxWidth().align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(fotosSeleccionadas.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(6.dp))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Añadir fotos", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Botón añadir más
                if (fotosSeleccionadas.size < 5) {
                    FilledIconButton(
                        onClick = { 
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.small)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Añadir")
                    }
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                OutlinedTextField(
                    value = precio,
                    onValueChange = viewModel::onPrecioChange,
                    label = { Text("Precio (COP)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = viewModel::onStockChange,
                    label = { Text("Stock inicial") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Text("Mascota", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                Mascota.values().forEach { mascota ->
                    FilterChip(
                        selected = mascotaSeleccionada == mascota,
                        onClick = { viewModel.onMascotaChange(mascota) },
                        label = { Text(mascota.name) }
                    )
                }
            }

            Text("Categoría", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                Categoria.values().forEach { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { viewModel.onCategoriaChange(categoria) },
                        label = { Text(categoria.name.replace("_", " ")) }
                    )
                }
            }

            Spacer(Modifier.height(Spacing.large))

            Button(
                onClick = { viewModel.guardarProducto(onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && precio.isNotBlank()
            ) {
                Text("Guardar Producto")
            }
        }
    }
}
