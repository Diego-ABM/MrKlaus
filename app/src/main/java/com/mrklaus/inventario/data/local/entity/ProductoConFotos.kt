package com.mrklaus.inventario.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductoConFotos(
    @Embedded val producto: ProductoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "productoId"
    )
    val fotos: List<FotoProductoEntity>
)
