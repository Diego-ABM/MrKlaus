package com.mrklaus.inventario.util

import android.util.Log
import com.mrklaus.inventario.domain.repository.LogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogManager @Inject constructor(
    private val logRepository: LogRepository,
    private val scope: CoroutineScope
) {
    fun info(tag: String, msg: String) {
        Log.i(tag, msg)
        scope.launch(Dispatchers.IO) {
            logRepository.log("INFO", tag, msg)
        }
    }

    fun error(tag: String, msg: String, tr: Throwable? = null) {
        Log.e(tag, msg, tr)
        scope.launch(Dispatchers.IO) {
            logRepository.log("ERROR", tag, msg, tr)
        }
    }

    fun cleanup() {
        scope.launch(Dispatchers.IO) {
            logRepository.limpiarLogs()
        }
    }
}
