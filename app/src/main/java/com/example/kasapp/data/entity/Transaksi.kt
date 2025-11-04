package com.example.kasapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true)
    val idTransaksi: Int = 0,
    val tglTransaksi: Long = System.currentTimeMillis(), // Simpan sebagai timestamp (Long)
    val jlhTransaksi: Double, // Total harga
    val jenisPembayaran: String // "QRIS" atau "Tunai"
)
