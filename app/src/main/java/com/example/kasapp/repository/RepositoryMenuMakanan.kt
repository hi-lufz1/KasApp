package com.example.ucp2.repository

import com.example.kasapp.data.entity.MenuMakanan
import kotlinx.coroutines.flow.Flow


interface RepositoryMenuMakanan {
    suspend fun insertMenu(menuMakanan: MenuMakanan)

    fun getAllMenu(): Flow<List<MenuMakanan>>

    fun getMenuById(id: Int): Flow<MenuMakanan>

    suspend fun deleteMenu(menuMakanan: MenuMakanan)

    suspend fun updateMenu(menuMakanan: MenuMakanan)
}
