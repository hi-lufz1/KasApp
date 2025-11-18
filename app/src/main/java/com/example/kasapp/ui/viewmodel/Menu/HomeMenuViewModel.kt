package com.example.kasapp.ui.viewmodel.Menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.data.entity.MenuMakanan
import com.example.ucp2.repository.RepositoryMenuMakanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UiState untuk HomeMenuViewModel (KelolaMenuScreen)
 */
data class MenuUiState(
    val searchQuery: String = "",
    val selectedFilter: String = "Semua", // Semua, Makanan, Minuman
    val listMenu: List<MenuMakanan> = emptyList(), // Daftar yang ditampilkan ke UI
    val isLoading: Boolean = true, // Mulai dengan loading

    // --- TAMBAHAN UNTUK COUNT ---
    val countSemua: Int = 0,
    val countMakanan: Int = 0,
    val countMinuman: Int = 0
    // ----------------------------
)

/**
 * ViewModel untuk KelolaMenuScreen (Home)
 */
class HomeMenuViewModel(
    private val repository: RepositoryMenuMakanan
) : ViewModel() {

    // State utama yang diobservasi oleh UI
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    // State internal untuk menyimpan daftar menu asli dari database
    private val _originalList = MutableStateFlow<List<MenuMakanan>>(emptyList())

    init {
        viewModelScope.launch {
            repository.getAllMenu()
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { listFromDb ->
                    // Simpan daftar asli
                    _originalList.value = listFromDb

                    // --- TAMBAHAN: HITUNG JUMLAH UNTUK FILTER ---
                    val countSemua = listFromDb.size
                    val countMakanan = listFromDb.count { it.jenisMenu == "Makanan" }
                    val countMinuman = listFromDb.count { it.jenisMenu == "Minuman" }

                    // Langsung update state dengan hitungan baru
                    _uiState.update {
                        it.copy(
                            countSemua = countSemua,
                            countMakanan = countMakanan,
                            countMinuman = countMinuman
                        )
                    }
                    // ---------------------------------------------

                    // Terapkan filter (awal)
                    applyFilters()
                }
        }
    }

    /**
     * Dipanggil saat pengguna mengetik di search bar.
     */
    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    /**
     * Dipanggil saat pengguna menekan tombol filter (Semua, Makanan, Minuman).
     */
    fun onFilterChange(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilters()
    }

    /**
     * Fungsi internal untuk memfilter daftar menu.
     */
    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase()
        val filter = _uiState.value.selectedFilter

        val filteredList = _originalList.value.filter { menu ->
            val matchesFilter = (filter == "Semua" || menu.jenisMenu == filter)
            val matchesSearch = (query.isBlank() || menu.namaMenu.lowercase().contains(query))
            matchesFilter && matchesSearch
        }

        // Perbarui UiState dengan daftar yang sudah difilter
        _uiState.update {
            it.copy(
                listMenu = filteredList,
                isLoading = false // Selesai loading
            )
        }
    }

    /**
     * Menghapus menu dari database.
     */
    fun deleteMenu(menu: MenuMakanan) {
        viewModelScope.launch {
            repository.deleteMenu(menu)
        }
    }
}
