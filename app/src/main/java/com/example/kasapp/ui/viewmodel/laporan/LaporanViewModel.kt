package com.example.kasapp.ui.viewmodel.laporan

import com.example.kasapp.data.entity.Transaksi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.repository.RepositoryTransaksi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LaporanUiState(
    val isLoading: Boolean = false,
    val transaksiList: List<Transaksi> = emptyList(),
    val totalPendapatan: Double = 0.0,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val error: String? = null
)

class LaporanViewModel(
    private val repositoryTransaksi: RepositoryTransaksi
) : ViewModel() {

    private val _uiState = MutableStateFlow(LaporanUiState())
    val uiState: StateFlow<LaporanUiState> = _uiState.asStateFlow()

    fun loadLaporan(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    startTime = startTime,
                    endTime = endTime
                )
            }

            combine(
                repositoryTransaksi.getTransaksiByDateRange(startTime, endTime),
                repositoryTransaksi.getTotalPendapatanByDateRange(startTime, endTime)
            ) { transaksi, total ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transaksiList = transaksi,
                        totalPendapatan = total ?: 0.0
                    )
                }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }.collect()
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Ambil semua transaksi
                repositoryTransaksi.getAllTransaksi()
                    .collect { transaksi ->
                        // Hitung total pendapatan
                        val total = transaksi.sumOf { it.jlhTransaksi }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transaksiList = transaksi,
                                totalPendapatan = total,
                                startTime = null,
                                endTime = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

}
