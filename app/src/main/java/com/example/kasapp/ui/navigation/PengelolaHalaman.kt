package com.example.kasapp.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// Import semua destinasi
import com.example.kasapp.ui.navigation.AppHome
import com.example.kasapp.ui.navigation.HomeMenu
import com.example.kasapp.ui.navigation.InsertMenu
import com.example.kasapp.ui.navigation.UpdateMenu
import com.example.kasapp.ui.navigation.SuccessScreen
import com.example.kasapp.ui.navigation.Kasir
import com.example.kasapp.ui.navigation.RincianPesanan
import com.example.kasapp.ui.navigation.NotaPesanan
import com.example.kasapp.ui.navigation.Riwayat
// Import semua halaman View
import com.example.kasapp.ui.view.HomeView

import com.example.kasapp.ui.view.kasir.HomeKasirView
import com.example.kasapp.ui.view.kasir.NotaPesananView
import com.example.kasapp.ui.view.kasir.RincianPesananView
import com.example.kasapp.ui.view.menu.HomeMenuView
import com.example.kasapp.ui.view.menu.InsertMenuView
import com.example.kasapp.ui.view.menu.UpdateMenuView
// Import ViewModel
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
// --- IMPORT BARU ---
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.kasapp.ui.view.Riwayat.RiwayatView
import com.example.kasapp.ui.view.menu.SuccessView
import kotlinx.coroutines.delay

// -----------------

@Composable
fun PengelolaHalaman(
    navController: NavHostController = rememberNavController()
) {
    // Buat ViewModel Kasir di sini agar bisa dibagikan
    val kasirViewModel: KasirViewModel = viewModel(factory = ViewModelFactory.Factory)
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = AppHome.route,
    ) {
        // Rute: Halaman Utama (AppHome)
        composable(route = AppHome.route) {
            HomeView(
                onNavigateToKelolaMenu = { navController.navigate(HomeMenu.route) },
                onNavigateToKasir = { navController.navigate(Kasir.route) },
                onNavigateToRiwayat = { navController.navigate(Riwayat.route) }
            )
        }

        // Rute: Kelola Menu (HomeMenu)
        composable(route = HomeMenu.route) {
            HomeMenuView(
                onTambahMenuClick = { navController.navigate(InsertMenu.route) },
                onEditMenuClick = { idMenu -> navController.navigate("${UpdateMenu.route}/$idMenu") }
            )
        }

        // Rute: Tambah Menu (InsertMenu)
        composable(route = InsertMenu.route) {
            InsertMenuView(
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    navController.navigate(SuccessScreen.route)
                }
            )
        }

        // Rute: Update Menu
        composable(
            route = UpdateMenu.routeWithArgs,
            arguments = listOf(navArgument(UpdateMenu.argIdMenu) { type = NavType.IntType })
        ) {
            UpdateMenuView(
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    navController.navigate(SuccessScreen.route)
                }
            )
        }

        // Rute: Layar Sukses (Animasi)
        composable(route = SuccessScreen.route) {
            val previousRoute = navController.previousBackStackEntry?.destination?.route

            SuccessView (
                onNavigateBack = {
                    if (previousRoute == RincianPesanan.route) {
                        navController.navigate(NotaPesanan.route) {
                            popUpTo(Kasir.route) { inclusive = false }
                        }
                    }
                    else if (previousRoute == InsertMenu.route || previousRoute == UpdateMenu.route) {
                        navController.navigate(HomeMenu.route) {
                            popUpTo(HomeMenu.route) { inclusive = true }
                        }
                    }
                    else {
                        navController.popBackStack()
                    }
                }
            )
        }

        // Rute: Kasir
        composable(route = Kasir.route) {
            // --- HAPUS LaunchedEffect DARI SINI ---
            // ------------------------------------

            HomeKasirView(
                viewModel = kasirViewModel,
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = {
                    navController.navigate(RincianPesanan.route)
                }
            )
        }

        // Rute: Rincian Pesanan
        composable(route = RincianPesanan.route) {
            RincianPesananView (
                viewModel = kasirViewModel,
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    navController.navigate(SuccessScreen.route)
                }
            )
        }

        // Rute: Nota Pesanan
        composable(route = NotaPesanan.route) {
            val previousRoute = navController.previousBackStackEntry?.destination?.route

            NotaPesananView(
                viewModel = kasirViewModel,
                onSelesaiClick = {
                    // --- PERBAIKAN: Navigasi dulu, BARU clearCart ---
                    scope.launch {
                        // 1. Tentukan tujuan
                        val destinationRoute = if (previousRoute == Riwayat.route) {
                            Riwayat.route
                        } else {
                            Kasir.route
                        }

                        // 2. Navigasi di Main Thread
                        withContext(Dispatchers.Main) {
                            navController.popBackStack(route = destinationRoute, inclusive = false)
                        }

                        // 3. Beri jeda agar animasi navigasi selesai
                        delay(200L)

                        // 4. Baru bersihkan keranjang
                        kasirViewModel.clearCart()
                    }
                    // ------------------------------------------
                }
            )
        }

        // Rute: Riwayat
        composable(route = Riwayat.route) {
            // --- HAPUS LaunchedEffect DARI SINI ---
            // ------------------------------------

            RiwayatView(
                onBackClick = { navController.popBackStack() },
                onNotaClick = { idTransaksi ->
                    scope.launch {
                        kasirViewModel.loadCartFromHistory(idTransaksi)
                        withContext(Dispatchers.Main) {
                            navController.navigate(NotaPesanan.route)
                        }
                    }
                }
            )
        }
    }
}