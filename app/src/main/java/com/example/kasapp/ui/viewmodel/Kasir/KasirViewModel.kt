package com.example.kasapp.ui.viewmodel.Kasir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.data.entity.DetailTransaksi
import com.example.kasapp.data.entity.MenuMakanan
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.repository.RepositoryTransaksi
import com.example.ucp2.repository.RepositoryMenuMakanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Representasi item dalam keranjang belanja.
 */
data class CartItem(
    val menu: MenuMakanan,
    val quantity: Int = 1
) {
    val subtotal: Double
        get() = menu.hargaMenu * quantity
}

/**
 * UiState untuk layar Kasir dan Rincian Pesanan.
 */
data class KasirUiState(
    val listMenu: List<MenuMakanan> = emptyList(),
    val cart: List<CartItem> = emptyList(),
    val selectedFilter: String = "Semua", // "Semua", "Makanan", "Minuman"
    val isLoading: Boolean = true,
    val selectedPayment: String = "QRIS", // "QRIS", "Tunai"
    val lastTransactionTimestamp: Long? = null,
    val isHistoryLoading: Boolean = false // <-- State loading untuk nota riwayat
) {
    // Properti turunan untuk menghitung total
    val totalCartPrice: Double
        get() = cart.sumOf { it.subtotal }

    val totalCartItems: Int
        get() = cart.sumOf { it.quantity }
}

/**
 * ViewModel untuk Kasir dan Rincian Pesanan.
 */
class KasirViewModel(
    private val repositoryMenuMakanan: RepositoryMenuMakanan,
    private val repositoryTransaksi: RepositoryTransaksi
) : ViewModel() {

    private val _uiState = MutableStateFlow(KasirUiState())
    val uiState: StateFlow<KasirUiState> = _uiState.asStateFlow()

    // State internal untuk menyimpan daftar menu asli dari database
    private val _originalMenuList = MutableStateFlow<List<MenuMakanan>>(emptyList())
    // State internal untuk keranjang belanja
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    private val _filter = MutableStateFlow("Semua")

    init {
        // Gabungkan semua flow internal (menu, keranjang, filter) menjadi satu UiState
        viewModelScope.launch {
            combine(
                _originalMenuList,
                _cart,
                _filter
            ) { menuList, cart, filter ->
                val filteredList = if (filter == "Semua") {
                    menuList
                } else {
                    menuList.filter { it.jenisMenu == filter }
                }

                // Buat UiState baru
                KasirUiState(
                    listMenu = filteredList,
                    cart = cart,
                    selectedFilter = filter,
                    isLoading = _uiState.value.isLoading, // Ambil state loading saat ini
                    selectedPayment = _uiState.value.selectedPayment,
                    lastTransactionTimestamp = _uiState.value.lastTransactionTimestamp,
                    isHistoryLoading = _uiState.value.isHistoryLoading // Ambil state history loading
                )
            }.catch {
                _uiState.update { it.copy(isLoading = false, isHistoryLoading = false) }
            }.collect { state ->
                // Update UiState utama
                _uiState.value = state
            }
        }
        loadAllMenu()
    }

    /**
     * Memuat semua menu dari RepositoryMenuMakanan.
     */
    private fun loadAllMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Mulai loading
            repositoryMenuMakanan.getAllMenu()
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { menuList ->
                    _originalMenuList.value = menuList // Simpan daftar asli
                    _uiState.update { it.copy(isLoading = false) } // Selesai loading
                }
        }
    }

    /**
     * Mengubah filter kategori menu (Semua, Makanan, Minuman).
     */
    fun changeFilter(filter: String) {
        _filter.value = filter
    }

    /**
     * Menambahkan menu ke keranjang.
     */
    fun addItemToCart(menu: MenuMakanan) {
        _cart.update { currentCart ->
            val existingItem = currentCart.find { it.menu.idMenu == menu.idMenu }
            if (existingItem != null) {
                currentCart.map {
                    if (it.menu.idMenu == menu.idMenu) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                }
            } else {
                currentCart + CartItem(menu = menu, quantity = 1)
            }
        }
    }

    /**
     * Mengurangi jumlah item di keranjang.
     */
    fun decreaseItemInCart(item: CartItem) {
        _cart.update { currentCart ->
            if (item.quantity > 1) {
                currentCart.map {
                    if (it.menu.idMenu == item.menu.idMenu) {
                        it.copy(quantity = it.quantity - 1)
                    } else {
                        it
                    }
                }
            } else {
                currentCart.filterNot { it.menu.idMenu == item.menu.idMenu }
            }
        }
    }

    /**
     * Menghapus item dari keranjang (di halaman Rincian Pesanan).
     */
    fun removeItemFromCart(item: CartItem) {
        _cart.update { currentCart ->
            currentCart.filterNot { it.menu.idMenu == item.menu.idMenu }
        }
    }

    /**
     * Mengatur metode pembayaran yang dipilih.
     */
    fun selectPaymentMethod(method: String) {
        _uiState.update { it.copy(selectedPayment = method) }
    }

    /**
     * Menyelesaikan transaksi dan menyimpannya ke database.
     */
    fun saveTransaction(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.cart.isEmpty()) return@launch

            val timestamp = System.currentTimeMillis()

            // 1. Buat objek Transaksi (Header)
            val transaksi = Transaksi(
                jlhTransaksi = currentState.totalCartPrice,
                jenisPembayaran = currentState.selectedPayment,
                tglTransaksi = timestamp
            )

            // 2. Buat List<DetailTransaksi> (Item)
            val detailItems = currentState.cart.map { cartItem ->
                DetailTransaksi(
                    idTransaksi = 0,
                    idMenu = cartItem.menu.idMenu,
                    jumlah = cartItem.quantity,
                    hargaSaatTransaksi = cartItem.menu.hargaMenu
                )
            }

            // 3. Simpan ke repository
            repositoryTransaksi.simpanTransaksi(transaksi, detailItems)
            _uiState.update { it.copy(lastTransactionTimestamp = timestamp) }
            // clearCart() DIHAPUS DARI SINI

            // 4. Panggil callback sukses
            onSuccess()
        }
    }

    /**
     * Mengosongkan keranjang belanja.
     * Fungsi ini dipanggil dari onScreenResumed()
     */
    fun clearCart() {
        _cart.value = emptyList()
        _filter.value = "Semua"
        _uiState.update { it.copy(lastTransactionTimestamp = null) }
    }

    /**
     * Mengambil data transaksi lama dan memuatnya ke keranjang
     * agar bisa ditampilkan di NotaPesananView.
     */
    fun loadCartFromHistory(idTransaksi: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isHistoryLoading = true) } // Mulai Loading

            val trxWithDetails = repositoryTransaksi.getTransaksiWithDetail(idTransaksi)
                .filterNotNull()
                .first()

            // Konversi DetailTransaksi kembali ke CartItem
            val cartItems = trxWithDetails.detailTransaksi.mapNotNull { detail ->
                // Ambil data menu (nama, jenis) berdasarkan idMenu
                val menu = repositoryMenuMakanan.getMenuById(detail.idMenu)
                    .filterNotNull()
                    .first() // Asumsi menu masih ada

                if (menu != null) {
                    CartItem(
                        // Buat ulang objek MenuMakanan, tapi GANTI harganya
                        // dengan harga saat transaksi (history)
                        menu = menu.copy(hargaMenu = detail.hargaSaatTransaksi),
                        quantity = detail.jumlah
                    )
                } else {
                    null // Jika menu sudah dihapus, abaikan
                }
            }

            _cart.value = cartItems // Muat keranjang lama
            _uiState.update {
                it.copy(
                    selectedPayment = trxWithDetails.transaksi.jenisPembayaran,
                    lastTransactionTimestamp = trxWithDetails.transaksi.tglTransaksi,
                    isHistoryLoading = false // Selesai Loading
                )
            }
        }
    }

    /**
     * Dipanggil saat HomeKasirView atau RiwayatView muncul (resume).
     * Ini membersihkan keranjang jika kita baru saja menyelesaikan transaksi.
     */
    fun onScreenResumed() {
        // Jika lastTransactionTimestamp ada DAN keranjang tidak kosong,
        // berarti kita baru selesai transaksi (dari NotaPesananView)
        if (_uiState.value.lastTransactionTimestamp != null && _cart.value.isNotEmpty()) {
            clearCart()
        }

        // Jika kita memuat riwayat, isHistoryLoading true.
        // Saat kita kembali, kita harus setel false agar tidak loading terus.
        if (_uiState.value.isHistoryLoading) {
            _uiState.update { it.copy(isHistoryLoading = false) }
        }
    }
}