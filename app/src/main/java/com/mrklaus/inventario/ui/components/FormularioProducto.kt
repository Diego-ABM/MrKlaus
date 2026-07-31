package com.mrklaus.inventario.ui.components

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
import coil3.compose.AsyncImage
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormularioProducto(
    nombre: String,
    onNombreChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    precioCompra: String,
    onPrecioCompraChange: (String) -> Unit,
    precioVenta: String,
    onPrecioVentaChange: (String) -> Unit,
    stock: String,
    onStockChange: (String) -> Unit,
    pesoKg: String,
    onPesoKgChange: (String) -> Unit,
    mascota: Mascota,
    onMascotaChange: (Mascota) -> Unit,
    categoria: Categoria,
    onCategoriaChange: (Categoria) -> Unit,
    fotos: List<Any>,
    onAddFoto: (android.net.Uri) -> Unit,
    onRemoveFoto: (Int) -> Unit,
    fechaVencimiento: Long?,
    onFechaVencimientoChange: (Long?) -> Unit,
    botonTexto: String,
    onBotonClick: () -> Unit,
    botonHabilitado: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAddFoto(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Carrusel de fotos
        val pagerState = rememberPagerState { fotos.size.coerceAtLeast(1) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (fotos.isNotEmpty()) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = fotos[page],
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        FilledIconButton(
                            onClick = { onRemoveFoto(page) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(Spacing.small),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color.Black.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "Remover", tint = Color.White)
                        }
                    }
                }
                
                // Indicadores
                if (fotos.size > 1) {
                    Row(
                        Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(fotos.size) { iteration ->
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

            if (fotos.size < 5) {
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
            onValueChange = onNombreChange,
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            OutlinedTextField(
                value = precioCompra,
                onValueChange = onPrecioCompraChange,
                label = { Text("Costo (Compra)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = precioVenta,
                onValueChange = onPrecioVentaChange,
                label = { Text("Precio (Venta)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            OutlinedTextField(
                value = stock,
                onValueChange = onStockChange,
                label = { Text("Stock") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = pesoKg,
                onValueChange = onPesoKgChange,
                label = { Text("Peso (Kg)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Text("Mascota", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
            Mascota.entries.forEach { m ->
                FilterChip(
                    selected = mascota == m,
                    onClick = { onMascotaChange(m) },
                    label = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        Text("Categoría", style = MaterialTheme.typography.titleSmall)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            Categoria.entries.forEach { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { onCategoriaChange(cat) },
                    label = { Text(cat.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        Text("Fecha de Vencimiento", style = MaterialTheme.typography.titleSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(fechaVencimiento?.let { dateFormat.format(Date(it)) } ?: "Seleccionar Fecha")
            }
            
            if (fechaVencimiento != null) {
                TextButton(onClick = { onFechaVencimientoChange(null) }) {
                    Text("No aplica")
                }
            }
        }

        Spacer(Modifier.height(Spacing.large))

        Button(
            onClick = onBotonClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = botonHabilitado
        ) {
            Text(botonTexto)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onFechaVencimientoChange(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
