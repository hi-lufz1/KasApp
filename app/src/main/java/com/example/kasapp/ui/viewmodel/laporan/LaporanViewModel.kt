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
    val totalPerJenisPembayaran: Map<String, Double> = emptyMap(),
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

            repositoryTransaksi
                .getTransaksiByDateRange(startTime, endTime)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { transaksi ->

                    val totalPendapatan = transaksi.sumOf { it.jlhTransaksi }

                    val totalPerJenis = transaksi
                        .groupBy { it.jenisPembayaran }
                        .mapValues { (_, list) ->
                            list.sumOf { it.jlhTransaksi }
                        }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            transaksiList = transaksi,
                            totalPendapatan = totalPendapatan,
                            totalPerJenisPembayaran = totalPerJenis
                        )
                    }
                }
        }
    }


    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repositoryTransaksi.getAllTransaksi()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
                .collect { transaksi ->

                    val totalPendapatan = transaksi.sumOf { it.jlhTransaksi }

                    val totalPerJenis = transaksi
                        .groupBy { it.jenisPembayaran }
                        .mapValues { it.value.sumOf { t -> t.jlhTransaksi } }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            transaksiList = transaksi,
                            totalPendapatan = totalPendapatan,
                            totalPerJenisPembayaran = totalPerJenis,
                            startTime = null,
                            endTime = null
                        )
                    }
                }
        }
    }

}
