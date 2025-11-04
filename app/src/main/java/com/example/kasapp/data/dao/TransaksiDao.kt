package com.example.kasapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.kasapp.data.entity.DetailTransaksi
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.data.entity.TransaksiWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {

    /**
     * Menyimpan transaksi baru dan mengembalikan ID-nya.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: Transaksi): Long // Mengembalikan ID baru

    /**
     * Menyimpan daftar item detail ke database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanyakDetail(detail: List<DetailTransaksi>)

    /**
     * Fungsi helper untuk menjalankan insert Transaksi dan Detail-nya
     * dalam satu operasi database yang aman.
     */
    @Transaction
    suspend fun simpanTransaksiLengkap(
        transaksi: Transaksi,
        detailItems: List<DetailTransaksi>
    ) {
        // 1. Simpan Transaksi (header) dan dapatkan ID-nya
        val idTransaksiBaru = insertTransaksi(transaksi)

        // 2. Set ID Transaksi untuk semua item detail
        val detailDenganId = detailItems.map {
            // Kita tidak bisa langsung memodifikasi data class,
            // jadi kita buat objek baru jika idTransaksi belum ada
            if (it.idTransaksi == 0) {
                it.copy(idTransaksi = idTransaksiBaru.toInt())
            } else {
                it
            }
        }

        // 3. Simpan semua item detail
        insertBanyakDetail(detailDenganId)
    }

    /**
     * Mengambil semua Transaksi (header saja) untuk halaman Riwayat.
     * Diurutkan dari yang terbaru.
     */
    @Query("SELECT * FROM transaksi ORDER BY tglTransaksi DESC")
    fun getAllTransaksi(): Flow<List<Transaksi>>

    /**
     * Mengambil 1 Transaksi spesifik beserta semua Detail Item-nya.
     * Digunakan saat user mengklik satu item di Riwayat.
     */
    @Transaction // Diperlukan untuk query relasi
    @Query("SELECT * FROM transaksi WHERE idTransaksi = :id")
    fun getTransaksiWithDetail(id: Int): Flow<TransaksiWithDetails>

    /**
     * Menghapus 1 transaksi dari database.
     * DetailTransaksi akan ikut terhapus karena onDelete = ForeignKey.CASCADE
     */
    @Delete
    suspend fun deleteTransaksi(transaksi: Transaksi)

    // --- FUNGSI BARU UNTUK FILTER TANGGAL & PENDAPATAN ---

    /**
     * Mengambil semua transaksi dalam rentang waktu tertentu.
     * (Misal: dari 1 November 00:00 hingga 30 November 23:59)
     * @param startTime Timestamp (Long) awal
     * @param endTime Timestamp (Long) akhir
     */
    @Query("SELECT * FROM transaksi WHERE tglTransaksi BETWEEN :startTime AND :endTime ORDER BY tglTransaksi DESC")
    fun getTransaksiByDateRange(startTime: Long, endTime: Long): Flow<List<Transaksi>>

    /**
     * Menghitung total pendapatan (SUM) dalam rentang waktu tertentu.
     * Mengembalikan Double? (bisa jadi null jika tidak ada transaksi)
     */
    @Query("SELECT SUM(jlhTransaksi) FROM transaksi WHERE tglTransaksi BETWEEN :startTime AND :endTime")
    fun getTotalPendapatanByDateRange(startTime: Long, endTime: Long): Flow<Double?>
}