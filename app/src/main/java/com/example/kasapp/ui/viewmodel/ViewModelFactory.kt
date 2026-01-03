package com.example.kasapp.ui.viewmodel


import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kasapp.KasApp
import com.example.kasapp.data.drive.BackupDebounceHolder
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.Menu.HomeMenuViewModel
import com.example.kasapp.ui.viewmodel.Menu.InsertMenuViewModel
import com.example.kasapp.ui.viewmodel.Menu.UpdateMenuViewModel
import com.example.kasapp.ui.viewmodel.Riwayat.RiwayatViewModel
import com.example.kasapp.ui.viewmodel.laporan.LaporanViewModel

object ViewModelFactory {

    val Factory = viewModelFactory {
        // ChartViewModel
        initializer {
            ChartViewModel(
                kasApp().containerApp.chartRepository
            )
        }

        // LoginViewModel (butuh Application)
        initializer {
            LoginViewModel(
                application = kasApp(),
                backupRepository = kasApp().containerApp.backupRepository
            )
        }


        // Initializer untuk HomeMenuViewModel
        initializer {
            HomeMenuViewModel(
                kasApp().containerApp.repositoryMenuMakanan
            )
        }

        // Initializer untuk InsertMenuViewModel
        initializer {
            InsertMenuViewModel(
                kasApp().containerApp.repositoryMenuMakanan,
                onLocalDataChanged = {
                    BackupDebounceHolder.notifyChange(
                        kasApp().containerApp.backupRepository
                    )
                }
            )
        }

        // Initializer untuk UpdateMenuViewModel
        initializer {
            UpdateMenuViewModel(
                this.createSavedStateHandle(), // Untuk mengambil ID dari Navigasi
                kasApp().containerApp.repositoryMenuMakanan,
                onLocalDataChanged = {
                    BackupDebounceHolder.notifyChange(
                        kasApp().containerApp.backupRepository
                    )
                }
            )
        }

        // Initializer untuk KasirViewModel
        initializer {
            KasirViewModel(
                // KasirViewModel butuh DUA repository
                kasApp().containerApp.repositoryMenuMakanan,
                kasApp().containerApp.repositoryTransaksi,
                onLocalDataChanged = {
                    BackupDebounceHolder.notifyChange(
                        kasApp().containerApp.backupRepository
                    )
                }
            )
        }

        // Initializer untuk RiwayatViewModel
        initializer {
            RiwayatViewModel(
                kasApp().containerApp.repositoryTransaksi
            )
        }

        // Initializer untuk LaporanViewModel
        initializer {
            LaporanViewModel(
                kasApp().containerApp.repositoryTransaksi
            )
        }


        initializer {
            BackupViewModel(
                application = kasApp(),
                backupRepository = kasApp().containerApp.backupRepository
            )
        }

    }
}

/**
 * Fungsi ekstensi (helper) untuk mendapatkan Application class dari CreationExtras.
 */
fun CreationExtras.kasApp(): KasApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KasApp)

