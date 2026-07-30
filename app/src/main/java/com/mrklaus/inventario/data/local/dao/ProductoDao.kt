package com.mrklaus.inventario.data.local.dao

import androidx.room.*
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoConFotos
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.domain.model.Mascota
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun getCount(): Int

    @Transaction
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProductoConFotos>>

    @Transaction
    @Query("SELECT * FROM productos WHERE mascota = :mascota ORDER BY nombre ASC")
    fun getPorMascota(mascota: Mascota): Flow<List<ProductoConFotos>>

    @Transaction
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getPorId(id: Long): ProductoConFotos?

    @Transaction
    @Query("SELECT * FROM productos WHERE isFavorite = 1 ORDER BY nombre ASC")
    fun getFavoritos(): Flow<List<ProductoConFotos>>

    @Query("UPDATE productos SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorito(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductoEntity): Long

    @Update
    suspend fun actualizar(producto: ProductoEntity): Unit

    @Delete
    suspend fun eliminar(producto: ProductoEntity): Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFoto(foto: FotoProductoEntity): Long

    @Query("DELETE FROM fotos_producto WHERE rutaArchivo = :ruta")
    suspend fun eliminarFotoPorRuta(ruta: String): Unit
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>): Unit
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFotos(fotos: List<FotoProductoEntity>): Unit

    @Query("DELETE FROM productos")
    suspend fun eliminarTodosLosProductos(): Unit

    @Query("DELETE FROM fotos_producto")
    suspend fun eliminarTodasLasFotos(): Unit

    @Transaction
    @Query("SELECT * FROM productos WHERE fechaVencimiento IS NOT NULL ORDER BY fechaVencimiento ASC")
    fun getProximosAVencer(): Flow<List<ProductoConFotos>>

    @Transaction
    @Query("SELECT * FROM productos WHERE cantidadStock <= 0 ORDER BY nombre ASC")
    fun getAgotados(): Flow<List<ProductoConFotos>>

    @Transaction
    @Query("SELECT * FROM productos WHERE pedirAlProveedor = 1 ORDER BY nombre ASC")
    fun getParaPedido(): Flow<List<ProductoConFotos>>

    @Query("UPDATE productos SET pedirAlProveedor = :pedir, notaPedido = :nota WHERE id = :id")
    suspend fun actualizarEstadoPedido(id: Long, pedir: Boolean, nota: String?)

    @Query("UPDATE productos SET cantidadStock = :nuevoStock WHERE id = :id")
    suspend fun actualizarStock(id: Long, nuevoStock: Int)
}
