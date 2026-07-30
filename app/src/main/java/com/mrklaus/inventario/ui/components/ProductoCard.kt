package com.mrklaus.inventario.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.ui.theme.Spacing
import java.util.concurrent.TimeUnit

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleFavorite: () -> Unit = {},
) {
    val vencimientoInfo = producto.fechaVencimiento?.let {
        val diff = it - System.currentTimeMillis()
        val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
        val color = when {
            days <= 30 -> Color.Red
            days <= 60 -> Color(0xFFFFA000)
            else -> Color(0xFF4CAF50)
        }
        Triple(days, color, true)
    } ?: Triple(0, Color.Transparent, false)

    val (daysLeft, vencimientoColor, hasVencimiento) = vencimientoInfo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(Spacing.medium)
                .height(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                model = producto.fotos.firstOrNull(),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = Spacing.medium),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (producto.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (producto.isFavorite) Color.Red else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (producto.pesoKg != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(start = Spacing.small)
                        ) {
                            Text(
                                text = "${producto.pesoKg}kg",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = "${producto.precio} COP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (producto.cantidadStock <= producto.stockMinimo) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Stock Bajo",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = " Stock Bajo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    if (hasVencimiento) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp, start = Spacing.small)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Vencimiento",
                                tint = vencimientoColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = " $daysLeft días",
                                style = MaterialTheme.typography.labelSmall,
                                color = vencimientoColor
                            )
                        }
                    }
                }

                Text(
                    text = "Stock: ${producto.cantidadStock} | ${producto.categoria.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
