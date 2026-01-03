package com.example.kasapp.data.drive

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
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
            try {
                val dbPath = context.getDatabasePath("KasAppDatabase")
                val walFile = File(dbPath.path + "-wal")
                val shmFile = File(dbPath.path + "-shm")

                Log.d("BackupDebug", "=== MULAI BACKUP ===")

                // LANGKAH 1: Checkpoint DULU saat DB masih terbuka
                val db = KasAppDatabase.getDatabase(context)
                val sqliteDb = db.openHelper.writableDatabase

                Log.d("BackupDebug", "Checkpoint saat DB masih aktif...")
                sqliteDb.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)")).use { cursor ->
                    if (cursor.moveToFirst()) {
                        val busy = cursor.getInt(0)
                        val log = cursor.getInt(1)
                        val checkpointed = cursor.getInt(2)
                        Log.d("BackupDebug", "Checkpoint aktif: busy=$busy, log=$log, checkpointed=$checkpointed")
                    }
                }

                val walSizeBefore = if (walFile.exists()) walFile.length() else 0
                Log.d("BackupDebug", "WAL size sebelum close: $walSizeBefore bytes")

                // LANGKAH 2: BARU tutup semua koneksi
                KasAppDatabase.closeAll(context)
                Thread.sleep(300)

                val walSizeAfter = if (walFile.exists()) walFile.length() else 0
                Log.d("BackupDebug", "WAL size setelah close: $walSizeAfter bytes")

                // LANGKAH 3: Jika masih ada WAL, coba checkpoint final
                if (walSizeAfter > 0) {
                    Log.w("BackupDebug", "⚠️ WAL masih ada ($walSizeAfter bytes), force checkpoint...")

                    try {
                        // Buka TANPA WAL flag untuk checkpoint
                        val sqliteDb = android.database.sqlite.SQLiteDatabase.openDatabase(
                            dbPath.path,
                            null,
                            android.database.sqlite.SQLiteDatabase.OPEN_READWRITE
                        )

                        sqliteDb.rawQuery("PRAGMA wal_checkpoint(TRUNCATE)", null).use { cursor ->
                            if (cursor.moveToFirst()) {
                                val busy = cursor.getInt(0)
                                val log = cursor.getInt(1)
                                val checkpointed = cursor.getInt(2)
                                Log.d("BackupDebug", "Final checkpoint: busy=$busy, log=$log, checkpointed=$checkpointed")

                                if (log > 0 && checkpointed > 0) {
                                    Log.d("BackupDebug", "✅ $checkpointed frame berhasil di-flush!")
                                } else if (log == 0) {
                                    Log.w("BackupDebug", "⚠️ WAL sudah kosong (log=0), mungkin data hilang!")
                                }
                            }
                        }

                        sqliteDb.close()

                        val finalWalSize = if (walFile.exists()) walFile.length() else 0
                        Log.d("BackupDebug", "WAL size final: $finalWalSize bytes")

                    } catch (e: Exception) {
                        Log.e("BackupDebug", "Error final checkpoint: ${e.message}", e)
                    }
                }

                // LANGKAH 4: Verifikasi dan backup
                if (!dbPath.exists()) {
                    Log.e("BackupDebug", "❌ DB file tidak ditemukan!")
                    return@withContext
                }

                val dbSize = dbPath.length()
                Log.d("BackupDebug", "DB size untuk backup: $dbSize bytes")

                // File sementara untuk backup
                val tempBackup = File(context.cacheDir, BACKUP_FILENAME)

                // Copy main DB
                dbPath.copyTo(tempBackup, overwrite = true)

                Log.d("BackupDebug", "✅ DB file copied, size: ${tempBackup.length()} bytes")

                val fileMetadata = DriveFile().apply {
                    name = BACKUP_FILENAME
                    mimeType = "application/octet-stream"
                }

                val mediaContent = FileContent("application/octet-stream", tempBackup)

                val existingId = findBackupFileId(drive)
                if (existingId == null) {
                    drive.files().create(fileMetadata, mediaContent).execute()
                    Log.d("BackupDebug", "Backup baru dibuat di Drive")
                } else {
                    drive.files().update(existingId, fileMetadata, mediaContent).execute()
                    Log.d("BackupDebug", "Backup existing diupdate di Drive")
                }

                // Simpan metadata backup time
                val currentTime = System.currentTimeMillis()
                uploadMetadata(drive, currentTime)
                LocalBackupMeta.saveBackupTime(context, currentTime)

                Log.d("BackupDebug", "Backup selesai!")

            } catch (e: Exception) {
                Log.e("BackupDebug", "ERROR saat backup: ${e.message}", e)
                throw e
            }
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
