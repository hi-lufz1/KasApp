package com.example.kasapp.ui.viewmodel.Menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.data.entity.MenuMakanan
import com.example.ucp2.repository.RepositoryMenuMakanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UiState untuk UpdateMenuViewModel
data class UpdateMenuUiState(
    val idMenu: Int = 0,
    val namaMenu: String = "",
    val hargaMenu: String = "",
    val jenisMenu: String = "", // Default kosong agar validasi awal jalan
    // State error
    val namaMenuError: String? = null,
    val hargaMenuError: String? = null,
    val jenisMenuError: String? = null
)

// Konstanta untuk argumen navigasi (pastikan sama dengan di AlamatNavigasi)
const val MENU_ID_ARG = "idMenu"

class UpdateMenuViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RepositoryMenuMakanan
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateMenuUiState())
    val uiState: StateFlow<UpdateMenuUiState> = _uiState.asStateFlow()

    private val menuId: Int = checkNotNull(savedStateHandle[MENU_ID_ARG])

    init {
        loadMenuData()
    }

    private fun loadMenuData() {
        viewModelScope.launch {
            repository.getMenuById(menuId)
                .filterNotNull()
                .first() // Ambil data menu sekali
                .let { menu ->
                    // Update state awal dengan data dari database
                    _uiState.update {
                        it.copy(
                            idMenu = menu.idMenu,
                            namaMenu = menu.namaMenu,
                            hargaMenu = menu.hargaMenu.toString(),
                            jenisMenu = menu.jenisMenu,
                            // Error direset saat load
                            namaMenuError = null,
                            hargaMenuError = null,
                            jenisMenuError = null
                        )
                    }
                    // Validasi data awal (seharusnya valid)
                    validateAllFields()
                }
        }
    }

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

    // Fungsi untuk memvalidasi semua field (dipanggil sebelum update)
    private fun validateAllFields(): Boolean {
        onNamaChange(uiState.value.namaMenu)
        onHargaChange(uiState.value.hargaMenu)
        onJenisChange(uiState.value.jenisMenu)

        // Cek apakah ada error di state
        return uiState.value.namaMenuError == null &&
                uiState.value.hargaMenuError == null &&
                uiState.value.jenisMenuError == null
    }

    // Fungsi updateMenu sekarang return Boolean (berhasil/gagal)
    suspend fun updateMenu(): Boolean {
        if (!validateAllFields()) { // Validasi ulang sebelum menyimpan
            return false // Jangan update jika tidak valid
        }

        // Jika valid, buat objek MenuMakanan
        val currentState = uiState.value
        val menuToUpdate = MenuMakanan(
            idMenu = currentState.idMenu, // Gunakan ID asli
            namaMenu = currentState.namaMenu,
            hargaMenu = currentState.hargaMenu.toDouble(),
            jenisMenu = currentState.jenisMenu
        )
        // Update ke repository
        repository.updateMenu(menuToUpdate)
        return true // Berhasil
    }
}

