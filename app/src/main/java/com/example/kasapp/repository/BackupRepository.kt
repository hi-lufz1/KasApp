package com.example.kasapp.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kasapp.data.drive.BackupDatabaseHelper
import com.example.kasapp.data.drive.GoogleDriveHelper
import com.example.kasapp.data.drive.LocalBackupMeta
import com.example.kasapp.worker.BackupWorker
import com.google.api.services.drive.Drive
import java.util.concurrent.TimeUnit

class BackupRepository(private val context: Context) {

    private fun getDriveService(): Drive =
        GoogleDriveHelper.getDriveService(context)

    suspend fun backupDatabase() {
        BackupDatabaseHelper.uploadDatabase(context, getDriveService())
    }

    suspend fun restoreDatabase(): Boolean {
        val drive = getDriveService()
        val success = BackupDatabaseHelper.restoreDatabase(context, drive)
        if (success) {
            val remoteTime = BackupDatabaseHelper.getBackupTime(drive)
            LocalBackupMeta.saveBackupTime(context, remoteTime)
        }
        return success
    }



    suspend fun shouldRestore(): Boolean {
        val drive = getDriveService()
        val remoteTime = BackupDatabaseHelper.getBackupTime(drive)
        val localTime = LocalBackupMeta.getBackupTime(context)

        Log.d("RestoreDebug", "remoteTime = $remoteTime | localTime = $localTime")

        return remoteTime > localTime
    }


    suspend fun shouldBackup(): Boolean {
        val drive = getDriveService()

        val remoteTime = BackupDatabaseHelper.getBackupTime(drive)
        val localTime = LocalBackupMeta.getBackupTime(context)

        return localTime > remoteTime   // artinya ada perubahan di lokal
    }

    fun scheduleDailyBackup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<BackupWorker>(
            1, TimeUnit.DAYS // hanya untuk test (minimal 15m)
        )
            .setConstraints(constraints)
            .addTag("AutoBackup")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "AutoBackupWork",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }


}
