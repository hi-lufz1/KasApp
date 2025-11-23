package com.example.kasapp.dependenciesinjection

import android.content.Context
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

    override val repositoryMenuMakanan: RepositoryMenuMakanan by lazy {
        LocalRepositoryMenuMakanan(
            KasAppDatabase.getDatabase(context).menuMakananDao(),
            context   // ⬅ Tambahkan context
        )
    }

    override val repositoryTransaksi: RepositoryTransaksi by lazy {
        LocalRepositoryTransaksi(
            KasAppDatabase.getDatabase(context).transaksiDao(),
            context   // ⬅ Tambahkan context
        )
    }

    override val chartRepository: ChartRepository by lazy {
        ChartRepository(
            KasAppDatabase.getDatabase(context).transaksiDao()
        )
    }

    override val backupRepository: BackupRepository by lazy {
        BackupRepository(context)
    }
}

