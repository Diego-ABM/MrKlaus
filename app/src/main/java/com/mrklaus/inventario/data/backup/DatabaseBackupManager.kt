package com.mrklaus.inventario.data.backup

import android.content.Context
import com.mrklaus.inventario.data.local.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    private val dbName = "mrklaus_database"

    suspend fun exportDatabase(targetFile: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Checkpoint to ensure all WAL data is in the main DB file
            database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close()
            
            val dbFile = context.getDatabasePath(dbName)
            if (!dbFile.exists()) return@withContext Result.failure(Exception("Database file not found"))

            dbFile.inputStream().use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importDatabase(sourceFile: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath(dbName)
            
            // Close DB before overwriting
            database.close()

            sourceFile.inputStream().use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            // Also clear WAL files to avoid conflicts with new DB
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
