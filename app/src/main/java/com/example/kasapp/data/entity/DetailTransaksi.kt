package com.example.kasapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

//@Entity(
//    tableName = "detail_transaksi",
//    primaryKeys = ["idTransaksi", "idMenu"], // Composite key
//    foreignKeys = [
//        ForeignKey(
//            entity = Transaksi::class,
//            parentColumns = ["idTransaksi"],
//            childColumns = ["idTransaksi"],
//            onDelete = ForeignKey.CASCADE // Jika Transaksi dihapus, detailnya ikut terhapus
//        ),
//        ForeignKey(
//            entity = MenuMakanan::class,
//            parentColumns = ["idMenu"],
//            childColumns = ["idMenu"],
//            onDelete = ForeignKey.RESTRICT // Jangan biarkan menu dihapus jika masih ada di transaksi
//        )
//    ],
//    indices = [Index(value = ["idTransaksi"]), Index(value = ["idMenu"])]
//)
//data class DetailTransaksi(
//    val idTransaksi: Int,
//    val idMenu: Int,
//    val jumlah: Int, // Misal: 2x Nutrisari
//    val hargaSaatTransaksi: Double // Simpan harga saat itu (PENTING!)
//)



// menu bisa dihapus dan detail transaksi tetap ada, tetap lengkap, dan tetap aman.
@Entity(
    tableName = "detail_transaksi",
    foreignKeys = [
        ForeignKey(
            entity = Transaksi::class,
            parentColumns = ["idTransaksi"],
            childColumns = ["idTransaksi"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idTransaksi")]
)
data class DetailTransaksi(
    @PrimaryKey(autoGenerate = true)
    val idDetail: Int = 0,

    val idTransaksi: Int,

    // id menu tidak wajib ada setelah menu dihapus
    val idMenu: Int?,

    // BIARKAN DETAILNYA TETAP LENGKAP
    val namaMenuSaatTransaksi: String,
    val jumlah: Int,
    val hargaSaatTransaksi: Int
)

