package com.example.kasapp.dependenciesinjection

import android.content.Context
import android.content.SharedPreferences
import com.example.kasapp.data.db.KasAppDatabase
import com.example.kasapp.repository.BackupRepository
import com.example.kasapp.repository.ChartRepository
import com.example.kasapp.repository.LocalRepositoryTransaksi
import com.example.kasapp.repository.RepositoryTransaksi
import com.example.ucp2.repository.LocalRepositoryMenuMakanan
import com.example.ucp2.repository.RepositoryMenuMakanan

interface InterfaceContainerApp {
    val repositoryMenuMakanan: RepositoryMenuMakanan
    val repositoryTransaksi: RepositoryTransaksi
    val chartRepository: ChartRepository
    val backupRepository: BackupRepository
}

class ContainerApp(private val context: Context) : InterfaceContainerApp {

    private val database: KasAppDatabase by lazy {
        KasAppDatabase.getDatabase(context)
    }

    override val repositoryMenuMakanan: RepositoryMenuMakanan by lazy {
        LocalRepositoryMenuMakanan(
            database.menuMakananDao(),
            context
        )
    }

    override val repositoryTransaksi: RepositoryTransaksi by lazy {
        LocalRepositoryTransaksi(
            database.transaksiDao(),
            context
        )
    }

    override val chartRepository: ChartRepository by lazy {
        ChartRepository(
            database.transaksiDao()
        )
    }

    override val backupRepository: BackupRepository by lazy {
        BackupRepository(context)
    }

}

