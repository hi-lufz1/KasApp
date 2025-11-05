package com.example.kasapp.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kasapp.KasApp // Pastikan ini adalah nama Application class Anda
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.Menu.HomeMenuViewModel
import com.example.kasapp.ui.viewmodel.Menu.InsertMenuViewModel
import com.example.kasapp.ui.viewmodel.Menu.UpdateMenuViewModel
import com.example.kasapp.ui.viewmodel.Riwayat.RiwayatViewModel

/**
 * Factory untuk semua ViewModel di aplikasi.
 * Ini akan mengambil repository dari Application Container (ContainerApp).
 */
object ViewModelFactory {

    val Factory = viewModelFactory {

        // Initializer untuk HomeMenuViewModel
        initializer {
            HomeMenuViewModel(
                kasApp().containerApp.repositoryMenuMakanan
            )
        }

        // Initializer untuk InsertMenuViewModel
        initializer {
            InsertMenuViewModel(
                kasApp().containerApp.repositoryMenuMakanan
            )
        }

        // Initializer untuk UpdateMenuViewModel
        initializer {
            UpdateMenuViewModel(
                this.createSavedStateHandle(), // Untuk mengambil ID dari Navigasi
                kasApp().containerApp.repositoryMenuMakanan
            )
        }

        // --- TAMBAHAN: Initializer untuk KasirViewModel ---
        initializer {
            KasirViewModel(
                // KasirViewModel butuh DUA repository
                kasApp().containerApp.repositoryMenuMakanan,
                kasApp().containerApp.repositoryTransaksi
            )
        }
        // ------------------------------------------------

        // --- TAMBAHAN: Initializer untuk RiwayatViewModel ---
        initializer {
            RiwayatViewModel(
                kasApp().containerApp.repositoryTransaksi
            )
        }
        // -------------------------------------------------
    }
}

/**
 * Fungsi ekstensi (helper) untuk mendapatkan Application class dari CreationExtras.
 */
fun CreationExtras.kasApp(): KasApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KasApp)

