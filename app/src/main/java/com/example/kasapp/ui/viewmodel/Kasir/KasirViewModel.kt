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
    val subtotal: Int
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
    val isHistoryLoading: Boolean = false
) {
    val totalCartPrice: Int
        get() = cart.sumOf { it.subtotal }

    val totalCartItems: Int
        get() = cart.sumOf { it.quantity }
}

/**
 * ViewModel untuk Kasir dan Rincian Pesanan.
 */
class KasirViewModel(
    private val repositoryMenuMakanan: RepositoryMenuMakanan,
    private val repositoryTransaksi: RepositoryTransaksi,
    private var currentTransactionId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(KasirUiState())
    val uiState: StateFlow<KasirUiState> = _uiState.asStateFlow()

    private val _originalMenuList = MutableStateFlow<List<MenuMakanan>>(emptyList())
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    private val _filter = MutableStateFlow("Semua")

    init {
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

                KasirUiState(
                    listMenu = filteredList,
                    cart = cart,
                    selectedFilter = filter,
                    isLoading = _uiState.value.isLoading,
                    selectedPayment = _uiState.value.selectedPayment,
                    lastTransactionTimestamp = _uiState.value.lastTransactionTimestamp,
                    isHistoryLoading = _uiState.value.isHistoryLoading
                )
            }.catch {
                _uiState.update { it.copy(isLoading = false, isHistoryLoading = false) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun loadAllMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repositoryMenuMakanan.getAllMenu()
                .catch {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { menuList ->
                    _originalMenuList.value = menuList
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun changeFilter(filter: String) {
        _filter.value = filter
    }

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

    fun removeItemFromCart(item: CartItem) {
        _cart.update { currentCart ->
            currentCart.filterNot { it.menu.idMenu == item.menu.idMenu }
        }
    }

    fun selectPaymentMethod(method: String) {
        _uiState.update { it.copy(selectedPayment = method) }
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.cart.isEmpty()) return@launch

            val timestamp = System.currentTimeMillis()

            val transaksi = Transaksi(
                jlhTransaksi = currentState.totalCartPrice,
                jenisPembayaran = currentState.selectedPayment,
                tglTransaksi = timestamp
            )

            val detailItems = currentState.cart.map { cartItem ->
                DetailTransaksi(
                    idDetail = 0,
                    idTransaksi = 0,
                    idMenu = cartItem.menu.idMenu,
                    namaMenuSaatTransaksi = cartItem.menu.namaMenu,
                    jumlah = cartItem.quantity,
                    hargaSaatTransaksi = cartItem.menu.hargaMenu
                )
            }

            repositoryTransaksi.simpanTransaksi(transaksi, detailItems)
            _uiState.update { it.copy(lastTransactionTimestamp = timestamp) }
            onSuccess()
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
        _filter.value = "Semua"
        _uiState.update { it.copy(lastTransactionTimestamp = null) }
    }

    fun loadCartFromHistory(idTransaksi: Int) {
        viewModelScope.launch {
            currentTransactionId = idTransaksi
            _uiState.update { it.copy(isHistoryLoading = true) }

            val trxWithDetails = repositoryTransaksi.getTransaksiWithDetail(idTransaksi)
                .filterNotNull()
                .first()

            val cartItems = trxWithDetails.detailTransaksi.map { detail ->
                val menu = detail.idMenu?.let { id ->
                    repositoryMenuMakanan.getMenuById(id).first()
                }

                CartItem(
                    menu = MenuMakanan(
                        idMenu = detail.idMenu ?: 0,
                        namaMenu = detail.namaMenuSaatTransaksi,
                        hargaMenu = detail.hargaSaatTransaksi,
                        jenisMenu = menu?.jenisMenu ?: "Tidak diketahui"
                    ),
                    quantity = detail.jumlah
                )
            }

            _cart.value = cartItems
            _uiState.update {
                it.copy(
                    selectedPayment = trxWithDetails.transaksi.jenisPembayaran,
                    lastTransactionTimestamp = trxWithDetails.transaksi.tglTransaksi,
                    isHistoryLoading = false
                )
            }
        }
    }

    fun onScreenResumed() {
        loadAllMenu()
        // Reset cart jika kita baru kembali dari save transaksi
        if (_uiState.value.lastTransactionTimestamp != null && _cart.value.isNotEmpty()) {
            clearCart()
        }
        if (_uiState.value.isHistoryLoading) {
            _uiState.update { it.copy(isHistoryLoading = false) }
        }
    }

    /**
     * Menghapus transaksi yang sedang dilihat.
     * PENTING: Jangan panggil clearCart() di sini untuk menghindari glitch UI menjadi 0.
     */
    suspend fun deleteCurrentTransaction() {
        currentTransactionId?.let { id ->
            val transaksiToDelete = Transaksi(
                idTransaksi = id,
                jlhTransaksi = 0,
                jenisPembayaran = "",
                tglTransaksi = 0L
            )

            // Hapus dari database (Database bersih)
            repositoryTransaksi.deleteTransaksi(transaksiToDelete)

            // Reset ID saja, jangan reset cart agar UI tidak berubah kosong
            currentTransactionId = null
        }
    }
}