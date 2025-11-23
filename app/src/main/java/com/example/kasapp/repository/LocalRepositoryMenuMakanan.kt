package com.example.ucp2.repository

import android.content.Context
import com.example.kasapp.data.dao.MenuMakananDao
import com.example.kasapp.data.drive.LocalBackupMeta
import com.example.kasapp.data.entity.MenuMakanan
import kotlinx.coroutines.flow.Flow

class LocalRepositoryMenuMakanan(
    private val menuMakananDao: MenuMakananDao,
    private val context: Context      // ⬅ Tambahkan context
) : RepositoryMenuMakanan {

    override suspend fun insertMenu(menuMakanan: MenuMakanan) {
        menuMakananDao.insertMenu(menuMakanan)
        LocalBackupMeta.saveBackupTime(context, System.currentTimeMillis())   // ⬅ Tambahkan
    }

    override fun getAllMenu(): Flow<List<MenuMakanan>> = menuMakananDao.getAllMenu()

    override fun getMenuById(id: Int): Flow<MenuMakanan> = menuMakananDao.getMenuById(id)

    override suspend fun deleteMenu(menuMakanan: MenuMakanan) {
        menuMakananDao.deleteMenu(menuMakanan)
        LocalBackupMeta.saveBackupTime(context, System.currentTimeMillis())   // ⬅ Tambahkan
    }

    override suspend fun updateMenu(menuMakanan: MenuMakanan) {
        menuMakananDao.updateMenu(menuMakanan)
        LocalBackupMeta.saveBackupTime(context, System.currentTimeMillis())   // ⬅ Tambahkan
    }
}
