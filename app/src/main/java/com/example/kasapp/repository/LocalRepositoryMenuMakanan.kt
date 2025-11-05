package com.example.ucp2.repository

import com.example.kasapp.data.dao.MenuMakananDao
import com.example.kasapp.data.entity.MenuMakanan
import kotlinx.coroutines.flow.Flow

class LocalRepositoryMenuMakanan(
    private val menuMakananDao: MenuMakananDao
) : RepositoryMenuMakanan {

    override suspend fun insertMenu(menuMakanan: MenuMakanan) {
        menuMakananDao.insertMenu(menuMakanan)
    }

    override fun getAllMenu(): Flow<List<MenuMakanan>> {
        return menuMakananDao.getAllMenu()
    }

    override fun getMenuById(id: Int): Flow<MenuMakanan> {
        return menuMakananDao.getMenuById(id)
    }

    override suspend fun deleteMenu(menuMakanan: MenuMakanan) {
        menuMakananDao.deleteMenu(menuMakanan)
    }

    override suspend fun updateMenu(menuMakanan: MenuMakanan) {
        menuMakananDao.updateMenu(menuMakanan)
    }
}
