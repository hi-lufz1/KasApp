package com.example.kasapp.repository

import android.content.Context
import com.example.kasapp.data.dao.TransaksiDao
import com.example.kasapp.data.drive.LocalBackupMeta
import com.example.kasapp.data.entity.DetailTransaksi
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.data.entity.TransaksiWithDetails
import kotlinx.coroutines.flow.Flow

class LocalRepositoryTransaksi(
    private val transaksiDao: TransaksiDao,
    private val context: Context        // ⬅ Tambahkan context
) : RepositoryTransaksi {

    override suspend fun simpanTransaksi(transaksi: Transaksi, detailItems: List<DetailTransaksi>) {
        transaksiDao.simpanTransaksiLengkap(transaksi, detailItems)
        LocalBackupMeta.saveBackupTime(context, System.currentTimeMillis())   // ⬅ Tambahkan
    }

    override fun getAllTransaksi(): Flow<List<Transaksi>> = transaksiDao.getAllTransaksi()

    override fun getTransaksiWithDetail(id: Int): Flow<TransaksiWithDetails> =
        transaksiDao.getTransaksiWithDetail(id)

    override suspend fun deleteTransaksi(transaksi: Transaksi) {
        transaksiDao.deleteTransaksi(transaksi)
        LocalBackupMeta.saveBackupTime(context, System.currentTimeMillis())   // ⬅ Tambahkan
    }

    override fun getTransaksiByDateRange(startTime: Long, endTime: Long): Flow<List<Transaksi>> =
        transaksiDao.getTransaksiByDateRange(startTime, endTime)

    override fun getTotalPendapatanByDateRange(startTime: Long, endTime: Long): Flow<Double?> =
        transaksiDao.getTotalPendapatanByDateRange(startTime, endTime)
}
