package com.example.kasapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.repository.BackupRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val backupRepository: BackupRepository,
) : AndroidViewModel(application) {

    private val _account = MutableStateFlow<GoogleSignInAccount?>(null)
    val account = _account.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()



    fun checkSignedIn() {
        _isLoading.value = true
        val account = GoogleSignIn.getLastSignedInAccount(getApplication())
        _account.value = account
        _isLoading.value = false
    }
    fun backupBeforeLogout(
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                backupRepository.backupDatabase()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setAccount(account: GoogleSignInAccount?) {
        _account.value = account
    }

    fun clearSession() {
        _account.value = null
    }

    fun clearLocalData(onDone: () -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.example.kasapp.data.db.KasAppDatabase.getDatabase(getApplication()).clearAllTables()
            com.example.kasapp.data.drive.LocalBackupMeta.clearBackupTime(getApplication()) // 🔹 Reset timestamp backup
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onDone()
            }
        }
    }



    // 🔹 Schedule backup jika user sudah punya izin Drive
    fun scheduleBackupIfAllowed(account: GoogleSignInAccount) {
        val scopes = arrayOf(
            Scope(DriveScopes.DRIVE_FILE),
            Scope(DriveScopes.DRIVE_APPDATA)
        )

        if (GoogleSignIn.hasPermissions(account, *scopes)) {
            backupRepository.scheduleDailyBackup()
        }
    }

    // 🔹 Auto restore setelah login
    fun autoRestore(onDone: (wasRestored: Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val needRestore = backupRepository.shouldRestore()
                if (needRestore) {
                    backupRepository.restoreDatabase()
                }
                onDone(needRestore) // ⬅ Return true jika restore dilakukan
            } catch (e: Exception) {
                e.printStackTrace()
                onDone(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun setLoading(value: Boolean) {
        _isLoading.value = value
    }
}
