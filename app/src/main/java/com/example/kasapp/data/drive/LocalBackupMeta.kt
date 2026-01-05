package com.example.kasapp.data.drive

import android.content.Context

object LocalBackupMeta {

    private const val PREF_NAME = "backup_meta_pref"
    private const val LAST_BACKUP_KEY = "last_backup_time"

    fun saveBackupTime(context: Context, time: Long) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(LAST_BACKUP_KEY, time)
            .apply()
    }

    fun getBackupTime(context: Context): Long {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(LAST_BACKUP_KEY, 0L)
    }

    fun clearBackupTime(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
