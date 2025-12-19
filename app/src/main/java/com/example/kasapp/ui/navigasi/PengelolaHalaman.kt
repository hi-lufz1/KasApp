package com.example.kasapp.ui.navigasi

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kasapp.ui.view.BackupScreen
import com.example.kasapp.ui.view.HomeProfileView
import com.example.kasapp.ui.view.HomeScreen
import com.example.kasapp.ui.view.LoginScreen
import com.example.kasapp.ui.view.Riwayat.RiwayatView
import com.example.kasapp.ui.view.kasir.HomeKasirView
import com.example.kasapp.ui.view.kasir.NotaPesananView
import com.example.kasapp.ui.view.kasir.RincianPesananView
import com.example.kasapp.ui.view.laporan.LaporanScreen
import com.example.kasapp.ui.view.menu.HomeMenuView
import com.example.kasapp.ui.view.menu.InsertMenuView
import com.example.kasapp.ui.view.menu.SuccessView
import com.example.kasapp.ui.view.menu.UpdateMenuView
import com.example.kasapp.ui.view.profile.TentangKamiView
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.LoginViewModel
import com.example.kasapp.ui.viewmodel.Riwayat.DetailRiwayatView
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PengelolaHalaman(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {

    val kasirViewModel: KasirViewModel = viewModel(factory = ViewModelFactory.Factory)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = context as Activity

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(activity, gso)

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        // 🔹 Tambahkan argumen name & email
        composable(
            route = "home/{name}/{email}",
            arguments = listOf(
                navArgument("name") { defaultValue = "Pengguna" },
                navArgument("email") { defaultValue = "-" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val email = backStackEntry.arguments?.getString("email")

            HomeScreen(
                name = name,
                email = email,

                // Navigasi ke Riwayat
                onNavigateToRiwayat = {
                    navController.navigate("riwayat")
                },

                // Navigasi ke Kasir
                onNavigateToKasir = {
                    navController.navigate("kasir")
                },

                // Navigasi ke Kelola Menu
                onNavigateToKelolaMenu = {
                    navController.navigate("home_menu")
                },
                onNavigateToLaporan = {
                    navController.navigate("laporan")
                },

                onNavigateToBackup = {
                    navController.navigate("backup")
                },
                onNavigateToProfile = { name, email ->
                    navController.navigate(
                        "${Profile.route}/${Uri.encode(name)}/${Uri.encode(email)}"
                    )
                }

            )
        }
        // Rute: Kelola Menu (HomeMenu)
        composable(route = HomeMenu.route) {
            HomeMenuView(
                onBackClick = { navController.popBackStack() },
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

            SuccessView(
                onNavigateBack = {
                    if (previousRoute == RincianPesanan.route) {
                        navController.navigate(NotaPesanan.route) {
                            popUpTo(Kasir.route) { inclusive = false }
                        }
                    } else if (previousRoute == InsertMenu.route || previousRoute == UpdateMenu.route) {
                        navController.navigate(HomeMenu.route) {
                            popUpTo(HomeMenu.route) { inclusive = true }
                        }
                    } else {
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
                },
//                onNavigateToRiwayat = {
//                    navController.navigate("riwayat")
//                },
            )
        }

        composable("laporan") {
            LaporanScreen(onBackClick = { navController.popBackStack() })
        }


        // Rute: Rincian Pesanan
        composable(route = RincianPesanan.route) {
            RincianPesananView(
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
                            navController.navigate(DetailRiwayat.route)
                        }
                    }
                }
            )
        }

        composable(route = DetailRiwayat.route) {
            DetailRiwayatView(
                viewModel = kasirViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Backup.route) {
            BackupScreen(onBackClick = { navController.popBackStack() })
        }

        // --- PROFILE ---
        composable(
            route = Profile.routeWithArgs,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val name = backStackEntry.arguments?.getString("name")
            val email = backStackEntry.arguments?.getString("email")

            HomeProfileView(
                name = name,
                email = email,
                onBackClick = { navController.popBackStack() },
                onTentangKamiClick = {
                    navController.navigate(TentangKami.route)
                },
                onKeluarAkunClick = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        loginViewModel.clearSession()

                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }


        // --- HALAMAN TENTANG KAMI (BARU) ---
        composable(route = TentangKami.route) {
            TentangKamiView(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
