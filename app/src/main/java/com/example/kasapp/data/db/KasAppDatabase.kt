package com.example.kasapp.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kasapp.data.dao.MenuMakananDao
import com.example.kasapp.data.dao.TransaksiDao
import com.example.kasapp.data.entity.DetailTransaksi // <-- IMPORT BARU
import com.example.kasapp.data.entity.MenuMakanan
import com.example.kasapp.data.entity.Transaksi // <-- IMPORT BARU


@Database(
    entities = [
        MenuMakanan::class,
        Transaksi::class,     // <-- TAMBAHKAN ENTITY
        DetailTransaksi::class  // <-- TAMBAHKAN ENTITY
    ],
    version = 2, // <-- NAIKKAN VERSI DATABASE KARENA ADA PERUBAHAN
    exportSchema = false
)
abstract class KasAppDatabase : RoomDatabase() {

    // 🧩 DAO
    abstract fun menuMakananDao(): MenuMakananDao
    abstract fun transaksiDao(): TransaksiDao // <-- TAMBAHKAN DAO BARU

    companion object {
        @Volatile
        private var Instance: KasAppDatabase? = null

        fun getDatabase(context: Context): KasAppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    KasAppDatabase::class.java,
                    "KasAppDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }

        fun forceReopen(context: Context) {
            synchronized(this) {
                Instance?.close()
                Instance = null
                Log.d("RestoreDebug", "DATABASE INSTANCE DI-RESET")
                getDatabase(context)
            }
        }
    }
}