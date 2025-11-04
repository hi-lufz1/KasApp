package com.example.kasapp.dependenciesinjection

import android.content.Context
import com.example.kasapp.repository.LocalRepositoryTransaksi
import com.example.kasapp.repository.RepositoryTransaksi
import com.example.keuanganapp.data.database.KasAppDatabase
import com.example.ucp2.repository.LocalRepositoryMenuMakanan
import com.example.ucp2.repository.RepositoryMenuMakanan

interface InterfaceContainerApp {
    val repositoryMenuMakanan: RepositoryMenuMakanan
    val repositoryTransaksi: RepositoryTransaksi
}

class ContainerApp(private val context: Context) : InterfaceContainerApp {
    override val repositoryMenuMakanan: RepositoryMenuMakanan by lazy {
        LocalRepositoryMenuMakanan(
            KasAppDatabase.getDatabase(context).menuMakananDao()
        )
    }

    override val repositoryTransaksi: RepositoryTransaksi by lazy {
        LocalRepositoryTransaksi(
            KasAppDatabase.getDatabase(context).transaksiDao()
        )
    }
}
