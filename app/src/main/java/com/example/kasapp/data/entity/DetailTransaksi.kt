package com.example.kasapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "detail_transaksi",
    primaryKeys = ["idTransaksi", "idMenu"], // Composite key
    foreignKeys = [
        ForeignKey(
            entity = Transaksi::class,
            parentColumns = ["idTransaksi"],
            childColumns = ["idTransaksi"],
            onDelete = ForeignKey.CASCADE // Jika Transaksi dihapus, detailnya ikut terhapus
        ),
        ForeignKey(
            entity = MenuMakanan::class,
            parentColumns = ["idMenu"],
            childColumns = ["idMenu"],
            onDelete = ForeignKey.RESTRICT // Jangan biarkan menu dihapus jika masih ada di transaksi
        )
    ],
    indices = [Index(value = ["idTransaksi"]), Index(value = ["idMenu"])]
)
data class DetailTransaksi(
    val idTransaksi: Int,
    val idMenu: Int,
    val jumlah: Int, // Misal: 2x Nutrisari
    val hargaSaatTransaksi: Double // Simpan harga saat itu (PENTING!)
)
