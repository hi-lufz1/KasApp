package com.example.kasapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class POJO (Plain Old Java Object) untuk menampung hasil
 * query relasi antara 1 Transaksi dengan Banyak DetailTransaksi.
 */
data class TransaksiWithDetails(
    // Mengambil satu objek Transaksi
    @Embedded
    val transaksi: Transaksi,

    // Mengambil daftar DetailTransaksi yang memiliki
    // idTransaksi yang sama dengan 'transaksi' di atas.
    @Relation(
        parentColumn = "idTransaksi",
        entityColumn = "idTransaksi"
    )
    val detailTransaksi: List<DetailTransaksi>
)