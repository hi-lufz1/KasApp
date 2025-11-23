package com.example.kasapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.repository.BackupRepository
import kotlinx.coroutines.launch

class BackupViewModel(
    application: Application,
    private val backupRepository: BackupRepository
) : AndroidViewModel(application) {

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

    fun restoreFromDrive(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                backupRepository.restoreDatabase()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
