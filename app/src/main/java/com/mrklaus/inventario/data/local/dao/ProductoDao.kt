package com.mrklaus.inventario.data.local.dao

import androidx.room.*
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoConFotos
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.domain.model.Mascota
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Transaction
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProductoConFotos>>

    @Transaction
    @Query("SELECT * FROM productos WHERE mascota = :mascota ORDER BY nombre ASC")
    fun getPorMascota(mascota: Mascota): Flow<List<ProductoConFotos>>

    @Transaction
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getPorId(id: Long): ProductoConFotos?

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
}
