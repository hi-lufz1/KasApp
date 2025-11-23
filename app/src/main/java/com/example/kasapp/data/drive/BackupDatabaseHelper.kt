package com.example.kasapp.data.drive

import android.content.Context
import android.util.Log
import com.example.kasapp.data.db.KasAppDatabase
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File as DriveFile
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

object BackupDatabaseHelper {

    private const val BACKUP_FILENAME = "kasapp_backup.db"
    private const val META_FILENAME = "backup_meta.json"

    private suspend fun findBackupFileId(drive: Drive): String? =
        withContext(Dispatchers.IO) {
            drive.files().list()
                .setQ("name='$BACKUP_FILENAME' and trashed=false")
                .execute()
                .files
                .firstOrNull()
                ?.id
        }

    suspend fun getBackupFile(drive: Drive): DriveFile? = withContext(Dispatchers.IO) {
        drive.files().list()
            .setQ("name='$BACKUP_FILENAME' and trashed=false")
            .execute()
            .files
            .firstOrNull()
    }

    suspend fun downloadBackup(context: Context, drive: Drive, file: DriveFile): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val dbPath = context.getDatabasePath("KasAppDatabase")

                // Tutup database dulu
                KasAppDatabase.getDatabase(context).close()

                // Hapus WAL & SHM supaya Room tidak membaca cache lama
                File(dbPath.path + "-wal").apply {
                    if (exists()) delete()
                }
                File(dbPath.path + "-shm").apply {
                    if (exists()) delete()
                }

                Log.d("RestoreDebug", "WAL & SHM DIHAPUS")

                // Download dan timpa file database
                FileOutputStream(dbPath).use { output ->
                    drive.files().get(file.id).executeMediaAndDownloadTo(output)
                }

                Log.d("RestoreDebug", "DATABASE FILE DIGANTI DARI DRIVE")

                true
            } catch (e: Exception) {
                Log.e("DownloadBackup", "ERROR: ${e.message}")
                false
            }
        }



    suspend fun uploadDatabase(context: Context, drive: Drive) {
        withContext(Dispatchers.IO) {
            // Tutup Room dulu agar semua data flush
            KasAppDatabase.getDatabase(context).close()

            val dbPath = context.getDatabasePath("KasAppDatabase")
            val wal = File(dbPath.path + "-wal")
            val shm = File(dbPath.path + "-shm")

            // File sementara untuk backup
            val tempBackup = File(context.cacheDir, BACKUP_FILENAME)

            // Copy main DB
            dbPath.copyTo(tempBackup, overwrite = true)

            // Copy WAL & SHM jika ada
            if (wal.exists()) wal.copyTo(File(context.cacheDir, wal.name), overwrite = true)
            if (shm.exists()) shm.copyTo(File(context.cacheDir, shm.name), overwrite = true)

            val fileMetadata = DriveFile().apply {
                name = BACKUP_FILENAME
                mimeType = "application/octet-stream"
            }

            val mediaContent = FileContent("application/octet-stream", tempBackup)

            val existingId = findBackupFileId(drive)
            if (existingId == null) {
                drive.files().create(fileMetadata, mediaContent).execute()
            } else {
                drive.files().update(existingId, fileMetadata, mediaContent).execute()
            }

            // Simpan metadata backup time
            val currentTime = System.currentTimeMillis()
            uploadMetadata(drive, currentTime)
            LocalBackupMeta.saveBackupTime(context, currentTime)
        }
    }


    suspend fun restoreDatabase(context: Context, drive: Drive): Boolean {
        return withContext(Dispatchers.IO) {
            Log.d("RestoreDebug", "RESTORE → CARI FILE DI DRIVE")

            val file = getBackupFile(drive)
            Log.d("RestoreDebug", "BACKUP FILE = ${file?.name ?: "TIDAK DITEMUKAN"}")

            if (file == null) return@withContext false

            val success = downloadBackup(context, drive, file)
            Log.d("RestoreDebug", "DOWNLOAD = $success")

            if (success) {
                Log.d("RestoreDebug", "FORCE RELOAD ROOM DATABASE")
                KasAppDatabase.forceReopen(context)
            }

            return@withContext success
        }
    }


    private suspend fun uploadMetadata(drive: Drive, time: Long) {
        withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("meta", ".json")
            tempFile.writeText("""{"last_backup_time": $time}""")

            val fileMetadata = DriveFile().apply {
                name = META_FILENAME
                mimeType = "application/json"
            }

            val mediaContent = FileContent("application/json", tempFile)

            val existingId = drive.files().list()
                .setQ("name='$META_FILENAME' and trashed=false")
                .execute()
                .files
                .firstOrNull()
                ?.id

            if (existingId == null) {
                drive.files().create(fileMetadata, mediaContent).execute()
            } else {
                drive.files().update(existingId, fileMetadata, mediaContent).execute()
            }
        }
    }

    suspend fun getBackupTime(drive: Drive): Long = withContext(Dispatchers.IO) {

        val metaId = drive.files().list()
            .setQ("name='$META_FILENAME' and trashed=false")
            .execute()
            .files
            .firstOrNull()
            ?.id ?: return@withContext 0L

        val tempFile = File.createTempFile("meta_temp", ".json")

        drive.files().get(metaId)
            .executeMediaAndDownloadTo(tempFile.outputStream())

        val json = tempFile.readText()
        val regex = """"last_backup_time"\s*:\s*(\d+)""".toRegex()
        regex.find(json)?.groupValues?.get(1)?.toLong() ?: 0L
    }
}
