package com.example.kasapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.kasapp.data.entity.MenuMakanan
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuMakananDao {

    // 🟢 Tambah data baru
    @Insert
    suspend fun insertMenu(menu: MenuMakanan)

    // 🟡 Ambil semua menu, urutkan berdasarkan nama
    @Query("SELECT * FROM menu_makanan ORDER BY namaMenu ASC")
    fun getAllMenu(): Flow<List<MenuMakanan>>

    // 🔵 Ambil satu menu berdasarkan ID
    @Query("SELECT * FROM menu_makanan WHERE idMenu = :id")
    fun getMenuById(id: Int): Flow<MenuMakanan>

    // 🟣 Hapus menu tertentu
    @Delete
    suspend fun deleteMenu(menu: MenuMakanan)

    // 🟠 Update data menu
    @Update
    suspend fun updateMenu(menu: MenuMakanan)
}