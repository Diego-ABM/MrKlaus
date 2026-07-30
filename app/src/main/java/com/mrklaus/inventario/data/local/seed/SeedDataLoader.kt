package com.mrklaus.inventario.data.local.seed

import android.content.Context
import com.mrklaus.inventario.data.local.dao.ProductoDao
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.domain.model.Categoria
import com.mrklaus.inventario.domain.model.Mascota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class SeedDataLoader(private val context: Context, private val dao: ProductoDao) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadSeedData() = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("seed/productos_seed.json").bufferedReader().use { it.readText() }
            val seedProductos = json.decodeFromString<List<SeedProducto>>(jsonString)

            val imageFiles: List<String> = context.assets.list("seed_images")?.sortedBy { it } ?: emptyList()
            
            // Directorio de destino para las imágenes
            val imagesDir = File(context.filesDir, "fotos_productos")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            seedProductos.forEachIndexed { index, seed ->
                val producto = ProductoEntity(
                    nombre = seed.nombre,
                    descripcion = seed.descripcion,
                    precio = seed.precio,
                    cantidadStock = seed.cantidadStock,
                    stockMinimo = seed.stockMinimo,
                    mascota = Mascota.valueOf(seed.mascota),
                    categoria = Categoria.valueOf(seed.categoria),
                    variante = seed.variante,
                    nota = seed.nota
                )
                
                val productoId = dao.insertar(producto)
                
                // Mapeo básico: 141 imágenes / 77 productos ≈ 1.8 fotos por producto
                // Asignamos imágenes de forma secuencial
                val startImgIndex = index * 2
                val endImgIndex = (startImgIndex + 1).coerceAtMost(imageFiles.size - 1)
                
                if (startImgIndex < imageFiles.size) {
                    for (i in startImgIndex..endImgIndex) {
                        val fileName = imageFiles[i]
                        val destFile = File(imagesDir, fileName)
                        
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
                                orden = i - startImgIndex
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
