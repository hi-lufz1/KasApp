package com.example.kasapp.worker



import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kasapp.repository.BackupRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn

class BackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("BackupWorker", "Worker mulai jalan...")

        return try {
            val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
            if (account == null) {
                Log.d("BackupWorker", "User belum login, worker retry")
                return Result.retry()
            }

            val repo = BackupRepository(applicationContext)

            if (!repo.shouldBackup()) {
                Log.d("BackupWorker", "Tidak perlu backup, data belum berubah")
                return Result.success()
            }

            repo.backupDatabase()
            Log.d("BackupWorker", "Backup berhasil")

            Result.success()

        } catch (e: Exception) {
            Log.e("BackupWorker", "Backup gagal: ${e.message}", e)
            Result.retry()
        }
    }

}
