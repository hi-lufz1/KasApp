package com.example.kasapp.ui.viewmodel.Riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.repository.RepositoryTransaksi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * UiState untuk Halaman Riwayat Transaksi.
 */
data class RiwayatUiState(
    val listTransaksi: List<Transaksi> = emptyList(),
    val filterJenis: String = "Semua", // "Semua", "QRIS", "Tunai"
    val tanggalDipilih: Long? = null, // Null = tampilkan semua tanggal
    val totalPendapatan: Int = 0,
    val isLoading: Boolean = true,
    val isFilteringByDate: Boolean = false // Flag untuk tahu apakah sedang filter tanggal
)

/**
 * ViewModel untuk Halaman Riwayat Transaksi.
 */
class RiwayatViewModel(
    private val repository: RepositoryTransaksi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiwayatUiState())
    val uiState: StateFlow<RiwayatUiState> = _uiState.asStateFlow()

    // Flow internal untuk filter jenis pembayaran
    private val _filterJenis = MutableStateFlow("Semua")
    // Flow internal untuk rentang tanggal (Mulai, Selesai) - null untuk semua tanggal
    private val _filterTanggal = MutableStateFlow<Pair<Long, Long>?>(null)

    init {
        viewModelScope.launch {
            // 1. Dapatkan data transaksi berdasarkan TANGGAL (atau semua jika null)
            val transaksiByTanggalFlow = _filterTanggal.flatMapLatest { rentangTanggal ->
                if (rentangTanggal == null) {
                    // Tampilkan SEMUA transaksi (tanpa filter tanggal)
                    repository.getAllTransaksi()
                        .catch {
                            _uiState.update { it.copy(isLoading = false) }
                            emit(emptyList())
                        }
                } else {
                    // Tampilkan transaksi berdasarkan rentang tanggal
                    val (mulai, selesai) = rentangTanggal
                    repository.getTransaksiByDateRange(mulai, selesai)
                        .catch {
                            _uiState.update { it.copy(isLoading = false) }
                            emit(emptyList())
                        }
                }
            }

            // 2. Gabungkan (combine) data dari (1) dengan filter JENIS
            combine(transaksiByTanggalFlow, _filterJenis) { listFromDb, jenis ->

                // 3. Terapkan filter JENIS (QRIS/Tunai) di memori
                val filteredList = if (jenis == "Semua") {
                    listFromDb
                } else {
                    listFromDb.filter { it.jenisPembayaran == jenis }
                }

                // 4. Hitung total pendapatan dari list yang sudah difilter
                val totalPendapatanFiltered = filteredList.sumOf { it.jlhTransaksi }

                // 5. Kembalikan UiState yang baru
                _uiState.update {
                    it.copy(
                        listTransaksi = filteredList,
                        totalPendapatan = totalPendapatanFiltered,
                        isLoading = false,
                        filterJenis = jenis,
                        isFilteringByDate = _filterTanggal.value != null
                    )
                }
            }.collect { }
        }
    }

    // Helper untuk mendapatkan timestamp awal hari (00:00:00)
    private fun getBatasAwalHari(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    // Helper untuk mendapatkan timestamp akhir hari (23:59:59)
    private fun getBatasAkhirHari(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    // Helper untuk mengambil rentang 24 jam dari 1 timestamp
    private fun getRentang24Jam(timestamp: Long): Pair<Long, Long> {
        val mulai = getBatasAwalHari(timestamp)
        val selesai = getBatasAkhirHari(timestamp)
        return Pair(mulai, selesai)
    }

    /**
     * Dipanggil saat filter jenis pembayaran (Semua, QRIS, Tunai) berubah.
     */
    fun onFilterJenisChange(filter: String) {
        _filterJenis.value = filter
    }

    /**
     * Dipanggil saat tanggal dipilih dari kalender.
     */
    fun onTanggalDipilihChange(tanggal: Long) {
        _uiState.update { it.copy(tanggalDipilih = tanggal) }
        _filterTanggal.value = getRentang24Jam(tanggal)
    }


    /**
     * Dipanggil saat user ingin melihat SEMUA transaksi (reset filter tanggal).
     */
    fun resetFilterTanggal() {
        _uiState.update { it.copy(tanggalDipilih = null) }
        _filterTanggal.value = null
    }


    /**
     * Menghapus transaksi.
     */
    fun deleteTransaksi(transaksi: Transaksi) {
        viewModelScope.launch {
            repository.deleteTransaksi(transaksi)
        }
    }
}