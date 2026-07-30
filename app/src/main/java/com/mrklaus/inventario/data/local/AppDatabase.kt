package com.mrklaus.inventario.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mrklaus.inventario.data.local.dao.ProductoDao
import com.mrklaus.inventario.data.local.entity.FotoProductoEntity
import com.mrklaus.inventario.data.local.entity.ProductoEntity
import com.mrklaus.inventario.data.local.seed.SeedDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ProductoEntity::class, FotoProductoEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao

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
                    // Temporal: Forzar recarga para asegurar que las nuevas imágenes se carguen
                    Log.d("AppDatabase", "Starting forced seed reload...")
                    val loader = SeedDataLoader(context, dao)
                    loader.loadSeedData(forceReload = true)
                }
            }
        }
    }
}
