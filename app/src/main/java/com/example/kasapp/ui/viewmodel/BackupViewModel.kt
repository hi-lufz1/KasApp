package com.example.kasapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.repository.BackupRepository
import kotlinx.coroutines.launch

class BackupViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BackupRepository(application)

    fun backupToDrive(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.backupDatabase()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }

    fun restoreFromDrive(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.restoreDatabase()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
