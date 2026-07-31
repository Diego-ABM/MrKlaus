package com.mrklaus.inventario.data.repository

import com.mrklaus.inventario.data.files.ImagenManager
import com.mrklaus.inventario.data.local.dao.ProductoDao
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoConFotos
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.domain.model.Mascota
import com.mrklaus.inventario.domain.model.Producto
import com.mrklaus.inventario.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao,
    private val imagenManager: ImagenManager
) : ProductoRepository {

    override fun getAllProductos(): Flow<List<Producto>> {
        return productoDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getProductosPorMascota(mascota: Mascota): Flow<List<Producto>> {
        return productoDao.getPorMascota(mascota).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getProductoByIdFlow(id: Long): Flow<Producto?> {
        return productoDao.getPorIdFlow(id).map { it?.toDomain() }
    }

    override suspend fun getProductoById(id: Long): Producto? {
        return productoDao.getPorId(id)?.toDomain()
    }

    override suspend fun insertarProducto(producto: Producto): Long {
        return productoDao.insertar(producto.toEntity())
    }

    override suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizar(producto.toEntity())
    }

    override suspend fun eliminarProducto(producto: Producto) {
        // Borrar archivos físicos
        producto.fotos.forEach { ruta ->
            imagenManager.borrarImagen(ruta)
        }
        productoDao.eliminar(producto.toEntity())
    }

    override suspend fun agregarFoto(productoId: Long, ruta: String) {
        val count = productoDao.getPorId(productoId)?.fotos?.size ?: 0
        productoDao.insertarFoto(
            FotoProductoEntity(
                productoId = productoId,
                rutaArchivo = ruta,
                orden = count
            )
        )
    }

    override suspend fun eliminarFoto(ruta: String) {
        imagenManager.borrarImagen(ruta)
        productoDao.eliminarFotoPorRuta(ruta)
    }

    override fun getFavoritos(): Flow<List<Producto>> {
        return productoDao.getFavoritos().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorito(productoId: Long) {
        productoDao.toggleFavorito(productoId)
    }

    override fun getProximosAVencer(): Flow<List<Producto>> {
        return productoDao.getProximosAVencer().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAgotados(): Flow<List<Producto>> {
        return productoDao.getAgotados().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getParaPedido(): Flow<List<Producto>> {
        return productoDao.getParaPedido().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun actualizarEstadoPedido(id: Long, pedir: Boolean, nota: String?) {
        productoDao.actualizarEstadoPedido(id, pedir, nota)
    }

    override suspend fun actualizarStock(id: Long, nuevoStock: Int) {
        productoDao.actualizarStock(id, nuevoStock)
    }

    override fun getCapitalInvertido(): Flow<Double> {
        return productoDao.getCapitalInvertido().map { it ?: 0.0 }
    }

    override fun getInversionPendiente(): Flow<Double> {
        return productoDao.getInversionPendiente().map { it ?: 0.0 }
    }

    private fun ProductoConFotos.toDomain(): Producto {
        return Producto(
            id = producto.id,
            nombre = producto.nombre,
            descripcion = producto.descripcion,
            precio = producto.precio,
            cantidadStock = producto.cantidadStock,
            stockMinimo = producto.stockMinimo,
            mascota = producto.mascota,
            categoria = producto.categoria,
            variante = producto.variante,
            nota = producto.nota,
            isFavorite = producto.isFavorite,
            fechaVencimiento = producto.fechaVencimiento,
            pesoKg = producto.pesoKg,
            tipoArena = producto.tipoArena,
            precioCompra = producto.precioCompra,
            pedirAlProveedor = producto.pedirAlProveedor,
            notaPedido = producto.notaPedido,
            fotos = fotos.sortedBy { it.orden }.map { it.rutaArchivo },
            fechaActualizacion = producto.fechaActualizacion
        )
    }

    private fun Producto.toEntity(): ProductoEntity {
        return ProductoEntity(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            cantidadStock = cantidadStock,
            stockMinimo = stockMinimo,
            mascota = mascota,
            categoria = categoria,
            variante = variante,
            nota = nota,
            isFavorite = isFavorite,
            fechaVencimiento = fechaVencimiento,
            pesoKg = pesoKg,
            tipoArena = tipoArena,
            precioCompra = precioCompra,
            pedirAlProveedor = pedirAlProveedor,
            notaPedido = notaPedido,
            fechaActualizacion = fechaActualizacion
        )
    }
}
