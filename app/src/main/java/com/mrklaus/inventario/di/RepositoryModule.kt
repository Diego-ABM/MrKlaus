package com.mrklaus.inventario.di

import com.mrklaus.inventario.data.repository.LogRepositoryImpl
import com.mrklaus.inventario.data.repository.PedidoRepositoryImpl
import com.mrklaus.inventario.data.repository.ProductoRepositoryImpl
import com.mrklaus.inventario.data.repository.VentaRepositoryImpl
import com.mrklaus.inventario.domain.repository.LogRepository
import com.mrklaus.inventario.domain.repository.PedidoRepository
import com.mrklaus.inventario.domain.repository.ProductoRepository
import com.mrklaus.inventario.domain.repository.VentaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductoRepository(
        productoRepositoryImpl: ProductoRepositoryImpl
    ): ProductoRepository

    @Binds
    @Singleton
    abstract fun bindVentaRepository(
        ventaRepositoryImpl: VentaRepositoryImpl
    ): VentaRepository

    @Binds
    @Singleton
    abstract fun bindLogRepository(
        logRepositoryImpl: LogRepositoryImpl
    ): LogRepository

    @Binds
    @Singleton
    abstract fun bindPedidoRepository(
        pedidoRepositoryImpl: PedidoRepositoryImpl
    ): PedidoRepository
}
