package com.example.kasapp.repository

import com.example.kasapp.data.dao.TransaksiDao
import com.example.kasapp.data.entity.DetailTransaksi
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.data.entity.TransaksiWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * Implementasi lokal dari Repository Transaksi.
 * Menggunakan TransaksiDao untuk berinteraksi dengan Room.
 */
class LocalRepositoryTransaksi(
    private val transaksiDao: TransaksiDao
) : RepositoryTransaksi {

    /**
     * Menyimpan transaksi lengkap (header dan detail).
     */
    override suspend fun simpanTransaksi(transaksi: Transaksi, detailItems: List<DetailTransaksi>) {
        // Panggil fungsi @Transaction dari DAO
        transaksiDao.simpanTransaksiLengkap(transaksi, detailItems)
    }

    /**
     * Mengambil semua riwayat transaksi.
     */
    override fun getAllTransaksi(): Flow<List<Transaksi>> {
        return transaksiDao.getAllTransaksi()
    }

    /**
     * Mengambil satu riwayat transaksi beserta detailnya.
     */
    override fun getTransaksiWithDetail(id: Int): Flow<TransaksiWithDetails> {
        return transaksiDao.getTransaksiWithDetail(id)
    }

    /**
     * Menghapus satu transaksi.
     */
    override suspend fun deleteTransaksi(transaksi: Transaksi) {
        transaksiDao.deleteTransaksi(transaksi)
    }

    /**
     * Mengambil riwayat transaksi berdasarkan rentang tanggal.
     */
    override fun getTransaksiByDateRange(startTime: Long, endTime: Long): Flow<List<Transaksi>> {
        return transaksiDao.getTransaksiByDateRange(startTime, endTime)
    }

    /**
     * Menghitung total pendapatan berdasarkan rentang tanggal.
     */
    override fun getTotalPendapatanByDateRange(startTime: Long, endTime: Long): Flow<Double?> {
        return transaksiDao.getTotalPendapatanByDateRange(startTime, endTime)
    }
}