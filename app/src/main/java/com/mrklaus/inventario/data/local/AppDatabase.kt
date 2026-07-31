package com.mrklaus.inventario.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mrklaus.inventario.data.local.dao.PedidoClienteDao
import com.mrklaus.inventario.data.local.dao.LogDao
import com.mrklaus.inventario.data.local.dao.ProductoDao
import com.mrklaus.inventario.data.local.dao.VentaDao
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.data.local.entity.VentaEntity
import com.mrklaus.inventario.data.local.entity.VentaItemEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteEntity
import com.mrklaus.inventario.data.local.entity.PedidoClienteItemEntity
import com.mrklaus.inventario.data.local.entity.LogEntryEntity
import com.mrklaus.inventario.data.local.seed.SeedDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductoEntity::class, 
        FotoProductoEntity::class, 
        VentaEntity::class, 
        VentaItemEntity::class,
        PedidoClienteEntity::class,
        PedidoClienteItemEntity::class,
        LogEntryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun pedidoClienteDao(): PedidoClienteDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mrklaus_database"
                )
                .addCallback(DatabaseCallback(context, scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("AppDatabase", "onCreate called")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d("AppDatabase", "onOpen called")
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.productoDao()
                    if (dao.getCount() == 0) {
                        Log.d("AppDatabase", "Database is empty, starting seed...")
                        val loader = SeedDataLoader(context, dao)
                        loader.loadSeedData(forceReload = false)
                    } else {
                        Log.d("AppDatabase", "Database already has data, skipping seed")
                    }
                }
            }
        }
    }
}
