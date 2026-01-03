package com.example.kasapp.data.drive

import com.example.kasapp.repository.BackupRepository
import kotlinx.coroutines.*

object BackupDebounceHolder {

    private const val DELAY = 60_000L

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var job: Job? = null

    fun notifyChange(backupRepository: BackupRepository) {
        job?.cancel()

        job = scope.launch {
            delay(DELAY)
            try {
                if (backupRepository.shouldBackup()) {
                    backupRepository.backupDatabase()
                }
            } catch (_: Exception) {
                // silent fail (by design)
            }
        }
    }
}
