package com.example.kasapp.ui.viewmodel.Menu

import androidx.lifecycle.ViewModel
import com.example.kasapp.data.entity.MenuMakanan
import com.example.ucp2.repository.RepositoryMenuMakanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// --- PERUBAHAN: Hapus isFormValid dari sini ---
data class InsertMenuUiState(
    val namaMenu: String = "",
    val hargaMenu: String = "",
    val jenisMenu: String = "", // Default kosong agar error muncul
    // State untuk pesan error
    val namaMenuError: String? = null,
    val hargaMenuError: String? = null,
    val jenisMenuError: String? = null
)
// ---------------------------------------------

class InsertMenuViewModel(
    private val repository: RepositoryMenuMakanan
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsertMenuUiState())
    val uiState: StateFlow<InsertMenuUiState> = _uiState.asStateFlow()

    // Fungsi update dan validasi digabung per field
    fun onNamaChange(nama: String) {
        val error = if (nama.isBlank()) "Nama menu harus diisi" else null
        _uiState.update {
            it.copy(namaMenu = nama, namaMenuError = error)
        }
    }

    fun onHargaChange(harga: String) {
        val error = when {
            harga.isBlank() -> "Harga menu harus diisi"
            harga.toDoubleOrNull() == null -> "Harga menu harus berupa angka"
            (harga.toDoubleOrNull() ?: 0.0) <= 0 -> "Harga menu harus lebih dari 0"
            else -> null // Valid
        }
        _uiState.update {
            it.copy(hargaMenu = harga, hargaMenuError = error)
        }
    }

    fun onJenisChange(jenis: String) {
        val error = if (jenis.isBlank()) "Kategori harus dipilih" else null
        _uiState.update {
            it.copy(jenisMenu = jenis, jenisMenuError = error)
        }
    }

    // Fungsi internal untuk mengecek semua validasi sebelum simpan
    private fun isInputValid(): Boolean {
        // Panggil validasi sekali lagi untuk memastikan state error terbaru
        onNamaChange(uiState.value.namaMenu)
        onHargaChange(uiState.value.hargaMenu)
        onJenisChange(uiState.value.jenisMenu)

        // Cek apakah ada error di state
        return uiState.value.namaMenuError == null &&
                uiState.value.hargaMenuError == null &&
                uiState.value.jenisMenuError == null
    }

    // Fungsi saveMenu sekarang return Boolean (berhasil/gagal)
    suspend fun saveMenu(): Boolean {
        if (!isInputValid()) {
            return false // Jangan simpan jika tidak valid
        }

        // Jika valid, buat objek MenuMakanan
        val menu = MenuMakanan(
            // idMenu akan autoGenerate oleh Room
            namaMenu = uiState.value.namaMenu,
            hargaMenu = uiState.value.hargaMenu.toDouble(), // Konversi di sini
            jenisMenu = uiState.value.jenisMenu
        )
        // Simpan ke repository
        repository.insertMenu(menu)
        return true // Berhasil
    }
}