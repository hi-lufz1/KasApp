package com.example.kasapp.repository

import com.example.kasapp.data.entity.DetailTransaksi
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.data.entity.TransaksiWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk Repository Transaksi.
 * Mendefinisikan fungsi-fungsi utama yang akan dipanggil oleh ViewModel.
 */
interface RepositoryTransaksi {
    /**
     * Menyimpan transaksi lengkap (header dan detail) ke database.
     * Fungsi ini akan memanggil DAO @Transaction.
     */
    suspend fun simpanTransaksi(transaksi: Transaksi, detailItems: List<DetailTransaksi>)

    /**
     * Mengambil semua riwayat transaksi (header saja).
     */
    fun getAllTransaksi(): Flow<List<Transaksi>>

    /**
     * Mengambil satu data transaksi lengkap dengan detail item-nya.
     */
    fun getTransaksiWithDetail(id: Int): Flow<TransaksiWithDetails>

    /**
     * Menghapus satu transaksi dari riwayat.
     */
    suspend fun deleteTransaksi(transaksi: Transaksi)

    /**
     * Mengambil semua riwayat transaksi dalam rentang waktu tertentu.
     */
    fun getTransaksiByDateRange(startTime: Long, endTime: Long): Flow<List<Transaksi>>

    /**
     * Menghitung total pendapatan (SUM) dalam rentang waktu tertentu.
     */
    fun getTotalPendapatanByDateRange(startTime: Long, endTime: Long): Flow<Double?>
}