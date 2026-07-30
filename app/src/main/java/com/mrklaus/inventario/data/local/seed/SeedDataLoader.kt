package com.mrklaus.inventario.data.local.seed

import android.content.Context
import android.util.Log
import com.mrklaus.inventario.data.local.dao.ProductoDao
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class SeedDataLoader(private val context: Context, private val dao: ProductoDao) {

    private val json = Json { ignoreUnknownKeys = true }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun loadSeedData(forceReload: Boolean = false) = withContext(Dispatchers.IO) {
        Log.d("SeedDataLoader", "Starting seed data loading (forceReload=$forceReload)...")
        try {
            if (forceReload) {
                Log.d("SeedDataLoader", "Cleaning database for re-seed")
                dao.eliminarTodasLasFotos()
                dao.eliminarTodosLosProductos()
            }

            val jsonString = context.assets.open("seed/productos_seed.json").bufferedReader().use { it.readText() }
            val seedProductos = json.decodeFromString<List<SeedProducto>>(jsonString)
            Log.d("SeedDataLoader", "Found ${seedProductos.size} products in JSON")

            // Directorio de destino para las imágenes
            val imagesDir = File(context.filesDir, "fotos_productos")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            seedProductos.forEach { seed ->
                val fechaVenc = seed.fechaVencimiento?.let {
                    try { dateFormat.parse(it)?.time } catch (e: Exception) { null }
                }

                val producto = ProductoEntity(
                    nombre = seed.nombre,
                    descripcion = seed.descripcion,
                    precio = seed.precio,
                    cantidadStock = seed.cantidadStock,
                    stockMinimo = seed.stockMinimo,
                    mascota = Mascota.valueOf(seed.mascota),
                    categoria = Categoria.valueOf(seed.categoria),
                    variante = seed.variante,
                    nota = seed.nota,
                    pesoKg = seed.pesoKg,
                    tipoArena = seed.tipoArena,
                    fechaVencimiento = fechaVenc
                )
                
                val productoId = dao.insertar(producto)
                Log.d("SeedDataLoader", "Inserted product: ${producto.nombre} with ID: $productoId")
                
                // Use explicit photos list from JSON if available
                val productImages = seed.fotos ?: emptyList()
                
                productImages.forEachIndexed { imgIndex, fileName ->
                    val destFile = File(imagesDir, fileName)
                    
                    try {
                        // Copiar imagen de assets a internal storage
                        context.assets.open("seed_images/$fileName").use { input ->
                            destFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        
                        dao.insertarFoto(
                            FotoProductoEntity(
                                productoId = productoId,
                                rutaArchivo = destFile.absolutePath,
                                orden = imgIndex
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("SeedDataLoader", "Error copying image $fileName: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
