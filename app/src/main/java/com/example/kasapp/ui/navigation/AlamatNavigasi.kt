package com.example.kasapp.ui.navigation

/**
 * Interface untuk mendefinisikan rute navigasi.
 */
interface Destinasi {
    val route: String
}

/**
 * Destinasi untuk Halaman Utama Aplikasi (Home)
 */
object AppHome : Destinasi {
    override val route = "app_home"
}

/**
 * Destinasi untuk Halaman Kelola Menu
 */
object HomeMenu : Destinasi {
    override val route = "home_menu"
}

/**
 * Destinasi untuk Halaman Tambah Menu
 */
object InsertMenu : Destinasi {
    override val route = "insert_menu"
}

/**
 * Destinasi untuk Halaman Update Menu (butuh ID)
 */
object UpdateMenu : Destinasi {
    override val route = "update_menu"
    const val argIdMenu = "idMenu" // Kunci argumen
    val routeWithArgs = "$route/{$argIdMenu}" // Rute lengkap
}

/**
 * Destinasi untuk Halaman Sukses
 */
object SuccessScreen : Destinasi {
    override val route = "success_screen"
}

/**
 * Destinasi untuk Halaman Kasir
 */
object Kasir : Destinasi {
    override val route = "kasir"
}

/**
 * Destinasi untuk Halaman Riwayat
 */
object Riwayat : Destinasi {
    override val route = "riwayat"
}

// --- TAMBAHAN YANG DIPERLUKAN ---

/**
 * Destinasi untuk Halaman Rincian Pesanan (Checkout)
 */
object RincianPesanan : Destinasi {
    override val route = "rincian_pesanan"
}

/**
 * Destinasi untuk Halaman Nota Pesanan (Struk)
 */
object NotaPesanan : Destinasi {
    override val route = "nota_pesanan"
}