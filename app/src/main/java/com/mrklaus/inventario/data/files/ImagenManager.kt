package com.mrklaus.inventario.data.files

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val folderName = "fotos_productos"

    suspend fun guardarImagen(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val directory = File(context.filesDir, folderName)
            if (!directory.exists()) directory.mkdirs()

            val fileName = "img_${UUID.randomUUID()}.jpg"
            val destFile = File(directory, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return@withContext destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun borrarImagen(path: String) = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
