package com.example.kasapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.repository.BackupRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackupViewModel(
    application: Application,
    private val backupRepository: BackupRepository
) : AndroidViewModel(application) {

    private var debounceJob: Job? = null

    private val DEBOUNCE_DELAY = 60_000L
    fun notifyLocalDataChanged() {
        debounceJob?.cancel()

        debounceJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY)

            try {
                if (backupRepository.shouldBackup()) {
                    backupRepository.backupDatabase()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun backupToDrive(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                backupRepository.backupDatabase()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun restoreFromDrive(
        force: Boolean = false,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!force) {
                    val needRestore = backupRepository.shouldRestore()
                    if (!needRestore) {
                        onResult(false, "LOCAL_NEWER")
                        return@launch
                    }
                }
                backupRepository.restoreDatabase()
                onResult(true, null)

            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

}
